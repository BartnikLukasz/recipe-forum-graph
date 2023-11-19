package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.CategoryRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateCategoryRequest;
import bartnik.master.app.graph.recipeforum.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequest request) {
        var category = Category.builder()
                .name(request.getName())
                .build();

        return categoryRepository.save(category);
    }

    public Category updateCategory(UUID id, UpdateCategoryRequest request) {
        var category = categoryRepository.getById(id);
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    public Category getRecipesForCategory(UUID id) {
        return categoryRepository.getById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
