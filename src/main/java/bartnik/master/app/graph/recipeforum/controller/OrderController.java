package bartnik.master.app.graph.recipeforum.controller;

import bartnik.master.app.graph.recipeforum.dto.request.OrderReportRequest;
import bartnik.master.app.graph.recipeforum.dto.request.OrderProductReportRequest;
import bartnik.master.app.graph.recipeforum.dto.request.OrderProductsRequest;
import bartnik.master.app.graph.recipeforum.dto.response.ProductReportResponse;
import bartnik.master.app.graph.recipeforum.dto.response.OrderReportResponse;
import bartnik.master.app.graph.recipeforum.mapper.OrderMapper;
import bartnik.master.app.graph.recipeforum.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/order-products")
    public ResponseEntity<Void> buyProducts(@RequestBody @Valid OrderProductsRequest request) {
        orderService.orderProducts(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/report")
    public ResponseEntity<OrderReportResponse> generateReport(@RequestBody @Valid OrderReportRequest request) {
        return ResponseEntity.ok(orderMapper.map(orderService.generateReport(request), request.getFrom(), request.getTo()));
    }

    @PostMapping("/products-report")
    public ResponseEntity<ProductReportResponse> generateProductReport(@RequestBody @Valid OrderProductReportRequest request) {
        return ResponseEntity.ok(orderMapper.mapProductReport(orderService.generateProductReport(request), request.getFrom(), request.getTo()));
    }
}
