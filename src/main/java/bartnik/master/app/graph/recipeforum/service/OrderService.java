package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.dto.request.OrderReportRequest;
import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.repository.LineItemRepository;
import bartnik.master.app.graph.recipeforum.repository.OrderRepository;
import bartnik.master.app.graph.recipeforum.repository.ProductRepository;
import bartnik.master.app.graph.recipeforum.dto.request.OrderProductReportRequest;
import bartnik.master.app.graph.recipeforum.dto.request.OrderProductsRequest;
import bartnik.master.app.graph.recipeforum.model.LineItem;
import bartnik.master.app.graph.recipeforum.model.Order;
import bartnik.master.app.graph.recipeforum.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Conditions;
import org.neo4j.cypherdsl.core.ResultStatement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.*;
import static org.neo4j.cypherdsl.core.Conditions.not;
import static org.neo4j.cypherdsl.core.Cypher.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final LineItemRepository lineItemRepository;
    private final ProductRepository productRepository;
    private final CustomUserRepository userRepository;

    @Transactional
    public void orderProducts(OrderProductsRequest request) {
        var currentUser = UserUtil.getCurrentUser();
        var user = userRepository.getByUsername(currentUser.getUsername());

        List<LineItem> items = request.getLineItems().stream()
                .map(lineItem -> {
            var product = productRepository.getById(lineItem.getProductId());
            if (product.getAvailability() < lineItem.getQuantity()) {
                return null;
            }
            product.setAvailability(product.getAvailability() - lineItem.getQuantity());
            productRepository.save(product);
            return LineItem.builder()
                    .product(product)
                    .quantity(lineItem.getQuantity())
                    .build();
        })
                .collect(Collectors.toList());
        items = lineItemRepository.saveAll(items);

        var order = Order.builder()
                .items(items)
                .user(user)
                .value(items.stream().map(
                        item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orderDate(LocalDate.now())
                .build();

        items.forEach(item -> item.setOrder(order));

        orderRepository.save(order);
    }

    public List<Order> generateReport(OrderReportRequest request) {
        return orderRepository.findAll(buildPredicate(request)).stream().toList();
    }

    public List<LineItem> generateProductReport(OrderProductReportRequest request) {
        Set<UUID> lineItemsIds = lineItemRepository.findAll(buildProductPredicate(request)).stream().map(LineItem::getId).collect(Collectors.toSet());
        return lineItemRepository.findAllById(lineItemsIds).stream().toList();

    }

    private ResultStatement buildPredicate(OrderReportRequest request) {
        var order = node("Order").named("o");
        var user = node("CustomUser").named("u");

        var condition = order.property("orderDate").gt(literalOf(request.getFrom()))
                .and(order.property("orderDate").lt(literalOf(request.getTo().plusDays(1))));

        var statement = match(user.relationshipTo(order, ORDERED.name()));

        if (!request.getUserIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getUserIds()) {
                tempCondition = tempCondition.or(user.property("id").eq(literalOf(id.toString())));
            }
            condition = condition.and(tempCondition);
        }
        if (!request.getExcludedUserIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getExcludedUserIds()) {
                tempCondition = tempCondition.and(not(user.property("id").eq(literalOf(id.toString()))));
            }
            condition = condition.and(tempCondition);
        }

        return statement.where(condition).returning(order).build();
    }

    private ResultStatement buildProductPredicate(OrderProductReportRequest request) {
        var order = node("Order").named("o");
        var user = node("CustomUser").named("u");
        var lineItem = node("LineItem").named("l");
        var product = node("Product").named("p");
        var productCategory = node("ProductCategory").named("pc");

        var condition = order.property("orderDate").gt(literalOf(request.getFrom().atStartOfDay()))
                .and(order.property("orderDate").lt(literalOf(request.getTo().plusDays(1).atStartOfDay())));

        var statement = match(user.relationshipTo(order, ORDERED.name())
                .relationshipFrom(lineItem, BELONGS_TO_ORDER.name())
                .relationshipFrom(product, BELONGS_TO_ITEM.name())
                .relationshipTo(productCategory, BELONGS_TO_PRODUCT_CATEGORY.name()));

        if (!request.getUserIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getUserIds()) {
                tempCondition = tempCondition.or(user.property("id").eq(literalOf(id.toString())));
            }
            condition = condition.and(tempCondition);
        }
        if (!request.getExcludedUserIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getExcludedUserIds()) {
                tempCondition = tempCondition.and(not(user.property("id").eq(literalOf(id.toString()))));
            }
            condition = condition.and(tempCondition);
        }
        if (!request.getProductIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getProductIds()) {
                tempCondition = tempCondition.or(product.property("id").eq(literalOf(id.toString())));
            }
            condition = condition.and(tempCondition);
        }
        if (!request.getExcludedProductIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getExcludedProductIds()) {
                tempCondition = tempCondition.and(not(product.property("id").eq(literalOf(id.toString()))));
            }
            condition = condition.and(tempCondition);
        }
        if (!request.getProductCategoriesIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getProductCategoriesIds()) {
                tempCondition = tempCondition.or(productCategory.property("id").eq(literalOf(id.toString())));
            }
            condition = condition.and(tempCondition);
        }
        if (!request.getExcludedProductCategoriesIds().isEmpty()) {
            var tempCondition = Conditions.noCondition();
            for (UUID id : request.getExcludedProductCategoriesIds()) {
                tempCondition = tempCondition.or(not(productCategory.property("id").eq(literalOf(id.toString()))));
            }
            condition = condition.and(tempCondition);
        }
        return statement.where(condition).returning(lineItem).build();
    }
}
