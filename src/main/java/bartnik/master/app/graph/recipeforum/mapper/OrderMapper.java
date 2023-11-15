package bartnik.master.app.graph.recipeforum.mapper;

import bartnik.master.app.graph.recipeforum.dto.response.*;
import bartnik.master.app.graph.recipeforum.model.LineItem;
import bartnik.master.app.graph.recipeforum.model.Order;
import bartnik.master.app.graph.recipeforum.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "productName", source = "product.name" )
    LineItemResponse map(LineItem lineItem);

    @Named("lineItemList")
    default List<LineItemResponse> map(List<LineItem> items) {
        return items.stream()
                .map(this::map)
                .toList();
    }

    @Mapping(target = "username", source = "user.username" )
    OrderResponse map(Order order);

    default OrderReportResponse map(List<Order> orders, LocalDate from, LocalDate to) {
        return OrderReportResponse.builder()
                .orders(orders.stream()
                        .map(this::map)
                        .toList())
                .value(orders.stream()
                        .map(Order::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .from(from)
                .to(to)
                .build();
    }

    default List<ProductsGroupedResponse> mapGrouped(List<LineItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(LineItem::getProduct))
                .entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<LineItem> lineItems = entry.getValue();
                    Integer quantity = lineItems.stream()
                            .mapToInt(LineItem::getQuantity)
                            .sum();
                    BigDecimal value = lineItems.stream()
                            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new ProductsGroupedResponse(product.getName(), quantity, value);
                })
                .collect(Collectors.toList());
    }

    default ProductReportResponse mapProductReport(List<LineItem> items, LocalDate from, LocalDate to) {
        var products = this.mapGrouped(items);
        var value = products.stream()
                .map(ProductsGroupedResponse::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProductReportResponse(products, value, from, to);
    }
}
