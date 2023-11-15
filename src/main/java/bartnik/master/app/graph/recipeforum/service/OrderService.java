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
import org.apache.commons.collections4.IteratorUtils;
import org.neo4j.cypherdsl.core.Expression;
import org.neo4j.cypherdsl.core.ResultStatement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.ORDERED;
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
                .orderDate(LocalDateTime.now())
                .build();

        items.forEach(item -> item.setOrder(order));

        orderRepository.save(order);
    }

    public List<Order> generateReport(OrderReportRequest request) {
        return orderRepository.findAll(buildPredicate(request)).stream().toList();
    }

    public List<LineItem> generateProductReport(OrderProductReportRequest request) {
//        return IteratorUtils.toList(lineItemRepository.findAll(buildProductPredicate(request)).iterator());
        return lineItemRepository.findAll();

    }

    private ResultStatement buildPredicate(OrderReportRequest request) {
        var order = node("Order").named("o");
        var user = node("CustomUser").named("u");

        var dateCondition = order.property("orderDate").gt(literalOf(request.getFrom().atStartOfDay()))
                .and(order.property("orderDate").lt(literalOf(request.getTo().plusDays(1).atStartOfDay())));

        var statement = match(order)
                .where(dateCondition);

        if (!request.getUserIds().isEmpty()) {
            statement = statement.match(order.relationshipTo(user, ORDERED.name())).where(user.property("id").in(literalOf(request.getUserIds().toString())));
        }
        if (!request.getExcludedUserIds().isEmpty()) {
            statement = statement.match(order.relationshipTo(user, ORDERED.name())).where(not(user.property("id").in(literalOf(request.getExcludedUserIds()))));
        }

        return statement.returning(order).build();
    }
//
//    private BooleanBuilder buildProductPredicate(OrderProductReportRequest request) {
//        var booleanBuilder = new BooleanBuilder();
//        booleanBuilder.and(lineItem.order.orderDate.after(request.getFrom().atStartOfDay())
//                .and((lineItem.order.orderDate.before(request.getTo().plusDays(1).atStartOfDay()))));
//
//        if (!request.getUserIds().isEmpty()) {
//            booleanBuilder.and(lineItem.order.user.id.in(request.getUserIds()));
//        }
//        if (!request.getExcludedUserIds().isEmpty()) {
//            booleanBuilder.and(lineItem.order.user.id.notIn(request.getExcludedUserIds()));
//        }
//        if (!request.getProductIds().isEmpty()) {
//            booleanBuilder.and(lineItem.product.id.in(request.getProductIds()));
//        }
//        if (!request.getExcludedProductIds().isEmpty()) {
//            booleanBuilder.and(lineItem.product.id.notIn(request.getExcludedProductIds()));
//        }
//        if (!request.getProductCategoriesIds().isEmpty()) {
//            booleanBuilder.and(lineItem.product.productCategory.id.in(request.getProductCategoriesIds()));
//        }
//        if (!request.getExcludedProductCategoriesIds().isEmpty()) {
//            booleanBuilder.and(lineItem.product.productCategory.id.notIn(request.getExcludedProductCategoriesIds()));
//        }
//        return booleanBuilder;
//    }
}
