package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.CategoryRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateCategoryRequest;
import bartnik.master.app.graph.recipeforum.model.Category;
import bartnik.master.app.graph.recipeforum.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.BELONGS_TO_CATEGORY;
import static org.neo4j.cypherdsl.core.Cypher.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final String CATEGORY = "Category";
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;

    public Category createCategory(CreateCategoryRequest request) {
        var category = Category.builder()
                .name(request.getName())
                .build();

        return categoryRepository.save(category);
    }

    public Category updateCategory(UUID id, UpdateCategoryRequest request) {
        var c = node(CATEGORY).named("c");
        var condition = c.property("id").eq(anonParameter(id.toString()));
        var updateStatement = match(c)
                .where(condition)
                .set(c.property("name").to(anonParameter(request.getName())))
                .returning(c)
                .build();
        return categoryRepository.findOne(updateStatement).orElseThrow();
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    public Category getRecipesForCategory(UUID id) {
        var c = node(CATEGORY).named("c");
        var r = node("Recipe").named("r");
        var belongsToCategory = r.relationshipTo(c, BELONGS_TO_CATEGORY.name());
        var condition = c.property("id").eq(anonParameter(id.toString()));
        var findStatement = match(c)
                .where(condition)
                .returning(c)
                .build();
        var findRecipesStatement = match(c)
                .where(condition)
                .match(belongsToCategory)
                .returning(r)
                .build();
        var category = categoryRepository.findOne(findStatement).orElseThrow();
        var recipes = recipeRepository.findAll(findRecipesStatement);
        category.setRecipes(new HashSet<>(recipes));
        return category;
    }

    public List<Category> getAllCategories() {
        var c = node(CATEGORY).named("c");
        var findStatement = match(c).returning(c).build();
        return categoryRepository.findAll(findStatement).stream().toList();
    }
}
