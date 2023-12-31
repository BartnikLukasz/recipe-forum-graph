package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.dto.request.CreateRecipeRequest;
import bartnik.master.app.graph.recipeforum.repository.CategoryRepository;
import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.repository.RecipeRepository;
import bartnik.master.app.graph.recipeforum.dto.request.RecipesFilterRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateRecipeRequest;
import bartnik.master.app.graph.recipeforum.model.Recipe;
import bartnik.master.app.graph.recipeforum.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final CustomUserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;

    public Recipe createRecipe(CreateRecipeRequest request) {
        var currentUser = UserUtil.getCurrentUser();
        var user = userRepository.getByUsername(currentUser.getUsername());
        var category = categoryRepository.getById(request.getCategory());

        var recipe = Recipe.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .ingredients(request.getIngredients())
                .tags(request.getTags())
                .user(user)
                .category(category)
                .build();



        return recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(UUID id) {
        return recipeRepository.getById(id);
    }

    public Page<Recipe> findRecipes(RecipesFilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize(), Sort.by(Sort.Direction.valueOf(filter.getDirection()), filter.getSortBy()));
        String sortProperty = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() + " " + pageable.getSort().get().findFirst().get().getDirection().name() : "numberOfLikes DESC";
        int skip = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        return recipeRepository.findAllFiltered(filter.getUserId(), filter.getTitleContains(), filter.getContentContains(),
                filter.getIngredientsContains(), filter.getTagsContains(), filter.getCategoryIds(), "r."+sortProperty, skip, limit, pageable);
    }

    public Recipe updateRecipe(UUID id, UpdateRecipeRequest request) {
        var recipe = recipeRepository.getById(id);
        var category = recipe.getCategory();

        if (!recipe.getCategory().getId().equals(request.getCategory())) {
            category = categoryRepository.getById(request.getCategory());
            recipe.setCategory(category);
        }
        recipe.apply(request);

        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(UUID id) {
        var currentUser = UserUtil.getCurrentUser();
        var recipe = recipeRepository.getById(id);

        if (!currentUser.getUsername().equals(recipe.getUser().getUsername()) || UserUtil.isCurrentUserAdmin()) {
            throw new AccessDeniedException("User is not admin or the owner of this recipe.");
        }

        recipeRepository.deleteById(id);
    }

    public Recipe rateRecipe(UUID id, boolean liked) {
        var currentUser = UserUtil.getCurrentUser();
        var user = userRepository.getByUsername(currentUser.getUsername());
        var recipe = recipeRepository.getById(id);

        if (!recipeRepository.isReactedByUser(id, user.getId(), liked)) {
            var likedRecipes = user.getLikedRecipes();
            var dislikedRecipes = user.getDislikedRecipes();
            var likedByUsers = recipe.getLikedByUsers();
            var dislikedByUsers = recipe.getDislikedByUsers();

            if (liked) {
                likedRecipes.add(recipe);
                likedByUsers.add(user);
                dislikedRecipes.removeIf(dislikedRecipe -> {
                    if (dislikedRecipe.equals(recipe)) {
                        recipe.setNumberOfDislikes(recipe.getNumberOfDislikes() - 1);
                        dislikedByUsers.remove(user);
                        return true;
                    }
                    return false;
                });
                recipe.setNumberOfLikes(recipe.getNumberOfLikes() + 1);
            } else {
                dislikedRecipes.add(recipe);
                dislikedByUsers.add(user);
                likedRecipes.removeIf(likedRecipe -> {
                    if (likedRecipe.equals(recipe)) {
                        recipe.setNumberOfLikes(recipe.getNumberOfLikes() - 1);
                        likedByUsers.remove(user);
                        return true;
                    }
                    return false;
                });
                recipe.setNumberOfDislikes(recipe.getNumberOfDislikes() + 1);
            }
            userRepository.save(user);
        }
        return recipeRepository.save(recipe);
    }
}
