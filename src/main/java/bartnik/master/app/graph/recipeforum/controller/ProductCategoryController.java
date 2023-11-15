package bartnik.master.app.graph.recipeforum.controller;

import bartnik.master.app.graph.recipeforum.dto.request.CreateProductCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateProductCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.response.ProductCategoryLiteResponse;
import bartnik.master.app.graph.recipeforum.dto.response.ProductCategoryResponse;
import bartnik.master.app.graph.recipeforum.mapper.ProductCategoryMapper;
import bartnik.master.app.graph.recipeforum.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product-category")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;
    private final ProductCategoryMapper mapper;

    @PostMapping
    public ResponseEntity<ProductCategoryLiteResponse> createProductCategory(@RequestBody @Validated CreateProductCategoryRequest request) {
        return ResponseEntity.ok(mapper.map(productCategoryService.createProductCategory(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryLiteResponse> updateProductCategory(@PathVariable UUID id, @RequestBody @Validated UpdateProductCategoryRequest request) {
        return ResponseEntity.ok(mapper.map(productCategoryService.updateProductCategory(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductCategoryLiteResponse> deleteProductCategory(@PathVariable UUID id) {
        productCategoryService.deleteProductCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductCategoryLiteResponse>> getAllCategories() {
        return ResponseEntity.ok(mapper.map(productCategoryService.getAllProductCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> getProductsForProductCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.map(productCategoryService.getProductsForProductCategory(id)));
    }

}
