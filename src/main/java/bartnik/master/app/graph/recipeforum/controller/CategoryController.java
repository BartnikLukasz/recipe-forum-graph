package bartnik.master.app.graph.recipeforum.controller;

import bartnik.master.app.graph.recipeforum.dto.request.CreateCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.response.CategoryLiteResponse;
import bartnik.master.app.graph.recipeforum.dto.response.CategoryResponse;
import bartnik.master.app.graph.recipeforum.mapper.CategoryMapper;
import bartnik.master.app.graph.recipeforum.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    @PostMapping
    public ResponseEntity<CategoryLiteResponse> createCategory(@RequestBody @Validated CreateCategoryRequest request) {
        return ResponseEntity.ok(mapper.map(categoryService.createCategory(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryLiteResponse> updateCategory(@PathVariable UUID id, @RequestBody @Validated UpdateCategoryRequest request) {
        return ResponseEntity.ok(mapper.map(categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryLiteResponse> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryLiteResponse>> getAllCategories() {
        return ResponseEntity.ok(mapper.map(categoryService.getAllCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getRecipesForCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.map(categoryService.getRecipesForCategory(id)));
    }

}
