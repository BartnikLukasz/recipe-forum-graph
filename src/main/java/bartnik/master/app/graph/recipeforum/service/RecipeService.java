package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.dto.request.CreateRecipeRequest;
import bartnik.master.app.graph.recipeforum.model.projections.RecipeLiteGet;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.ADDED;
import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.BELONGS_TO_CATEGORY;
import static org.neo4j.cypherdsl.core.Cypher.*;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.*;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private static final String USER = "CustomUser";
    private static final String RECIPE = "Recipe";
    private static final String CATEGORY = "Category";
    private final CustomUserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final CommentService commentService;

    public Recipe createRecipe(CreateRecipeRequest request) {
        var currentUser = UserUtil.getCurrentUser();
        var u = node(USER).named("u");
        var c = node(CATEGORY).named("c");

        var user = userRepository.findOne(match(u)
                .where(u.property("username").eq(anonParameter(currentUser.getUsername())))
                .returning(u)
                .build()).orElseThrow();

        var category = categoryRepository.findOne(match(c)
                .where(c.property("id").eq(anonParameter(request.getCategory().toString())))
                .returning(c)
                .build()).orElseThrow();

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
        var u = node(USER).named("u");
        var r = node(RECIPE).named("r");
        var c = node(CATEGORY).named("c");
        var belongsToCategory = c.relationshipFrom(r, BELONGS_TO_CATEGORY.name());
        var added = u.relationshipTo(r, ADDED.name());
        var condition = r.property("id").eq(anonParameter(id.toString()));

        var recipe = recipeRepository.findOne(match(r)
                .where(condition)
                .returning(r)
                .build()).orElseThrow();

        var category = categoryRepository.findOne(match(r)
                .where(condition)
                .match(belongsToCategory)
                .returning(c)
                .build()).orElseThrow();

        var user = userRepository.findOne(match(r)
                .where(condition)
                .match(added)
                .returning(u)
                .build()).orElseThrow();

        var comments = commentService.getCommentsForRecipe(id);

        recipe.setCategory(category);
        recipe.setUser(user);
        recipe.setComments(comments);

        return recipe;
    }

    public Page<RecipeLiteGet> findRecipes(RecipesFilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize(), Sort.by(Sort.Direction.valueOf(filter.getDirection()), filter.getSortBy()));
        String sortProperty = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() + " " + pageable.getSort().get().findFirst().get().getDirection().name() : "numberOfLikes DESC";
        int skip = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        return recipeRepository.findAllFiltered(filter.getUserId(), filter.getTitleContains(), filter.getContentContains(),
                filter.getIngredientsContains(), filter.getTagsContains(), filter.getCategoryIds(), "r."+sortProperty, skip, limit, pageable);
    }

    @Transactional
    public Recipe updateRecipe(UUID id, UpdateRecipeRequest request) {
        var r = node(RECIPE).named("r");
        var c = node(CATEGORY).named("c");
        var belongsToCategory = c.relationshipFrom(r, BELONGS_TO_CATEGORY.name());
        var condition = r.property("id").eq(anonParameter(id.toString()));

        var recipe = recipeRepository.findOne(match(r)
                .where(condition)
                .returning(r)
                .build()).orElseThrow();

        var category = categoryRepository.findOne(match(r)
                .where(condition)
                .match(belongsToCategory)
                .returning(c)
                .build()).orElseThrow();

        if (!category.getId().equals(request.getCategory())) {
            var cOld = node(CATEGORY).named("cOld");
            var cNew = node(CATEGORY).named("cNew");

            var belongsToOldCategory = cOld.relationshipFrom(r, BELONGS_TO_CATEGORY.name());
            var belongsToNewCategory = cNew.relationshipFrom(r, BELONGS_TO_CATEGORY.name());

            var statement = match(belongsToOldCategory)
                    .where(cOld.property("id").eq(anonParameter(category.getId().toString())))
                    .and(r.property("id").eq(anonParameter(id.toString())))
                    .match(cNew)
                    .where(cNew.property("id").eq(anonParameter(request.getCategory().toString())))
                    .create(belongsToNewCategory)
                    .delete(belongsToOldCategory)
                    .returning(cNew)
                    .build();

            category = categoryRepository.findOne(statement).orElseThrow();
        }

        var updateStatement = match(r)
                .where(condition)
                .set(r.property("title").to(anonParameter(request.getTitle())))
                .set(r.property("content").to(anonParameter(request.getContent())))
                .set(r.property("tags").to(anonParameter(request.getTags())))
                .set(r.property("ingredients").to(anonParameter(request.getIngredients())))
                .returning(r)
                .build();

        recipe = recipeRepository.findOne(updateStatement).orElseThrow();
        recipe.setCategory(category);

        return recipe;
    }

    public void deleteRecipe(UUID id) {
        var currentUser = UserUtil.getCurrentUser();
        var u = node(USER).named("u");
        var r = node(RECIPE).named("r");
        var added = u.relationshipTo(r, ADDED.name());
        var condition = r.property("id").eq(anonParameter(id.toString()));

        var user = userRepository.findOne(match(r)
                .where(condition)
                .match(added)
                .returning(u)
                .build()).orElseThrow();

        if (!currentUser.getUsername().equals(user.getUsername()) || UserUtil.isCurrentUserAdmin()) {
            throw new AccessDeniedException("User is not admin or the owner of this recipe.");
        }

        recipeRepository.deleteById(id);
    }

    @Transactional
    public Recipe rateRecipe(UUID id, boolean liked) {
        var currentUser = UserUtil.getCurrentUser();
        var u = node(USER).named("u");
        var r = node(RECIPE).named("r");
        var userCondition = u.property("username").eq(anonParameter(currentUser.getUsername()));
        var recipeCondition = r.property("id").eq(anonParameter(id.toString()));

        var user = userRepository.findOne(match(u).where(userCondition).returning(u).build()).orElseThrow();
        var recipe = recipeRepository.findOne(match(r).where(recipeCondition).returning(r).build()).orElseThrow();

        if (!recipeRepository.isReactedByUser(id, user.getId(), liked)) {
            var userIdCondition = u.property("id").eq(anonParameter(user.getId().toString()));
            var likedRecipeRelationship = u.relationshipTo(r, LIKED.name());
            var dislikedRecipeRelationship = u.relationshipTo(r, DISLIKED.name());
            var likedByUsersRelationship = r.relationshipFrom(u, LIKED.name());
            var dislikedByUsersRelationship = r.relationshipFrom(u, DISLIKED.name());

            var likedRecipes = recipeRepository.findAll(match(u)
                    .where(userIdCondition)
                    .match(likedRecipeRelationship)
                    .returning(r).build());
            var dislikedRecipes = recipeRepository.findAll(match(u)
                    .where(userIdCondition)
                    .match(dislikedRecipeRelationship)
                    .returning(r).build());

            if (liked) {
                var createLikeStatement = match(r).where(recipeCondition)
                                .match(u).where(userIdCondition)
                                .create(likedByUsersRelationship)
                                .build();
                recipeRepository.findOne(createLikeStatement);
                if (dislikedRecipes.contains(recipe)) {
                    var deleteDislikeStatement = match(r).where(recipeCondition)
                            .match(u).where(userIdCondition)
                            .match(dislikedByUsersRelationship)
                            .delete(dislikedByUsersRelationship)
                            .build();
                    recipeRepository.findOne(deleteDislikeStatement);
                    recipeRepository.findOne(match(r).where(recipeCondition).set(r.property("numberOfDislikes").to(anonParameter(recipe.getNumberOfDislikes() - 1))).build());
                }
                recipeRepository.findOne(match(r).where(recipeCondition).set(r.property("numberOfLikes").to(anonParameter(recipe.getNumberOfLikes() + 1))).build());
            } else {
                var createDislikeStatement = match(r).where(recipeCondition)
                        .match(u).where(userIdCondition)
                        .create(dislikedByUsersRelationship)
                        .build();
                recipeRepository.findOne(createDislikeStatement);
                if (likedRecipes.contains(recipe)) {
                    var deleteLikeStatement = match(r).where(recipeCondition)
                            .match(u).where(userIdCondition)
                            .match(likedByUsersRelationship)
                            .delete(likedByUsersRelationship)
                            .build();
                    recipeRepository.findOne(deleteLikeStatement);
                    recipeRepository.findOne(match(r).where(recipeCondition).set(r.property("numberOfLikes").to(anonParameter(recipe.getNumberOfLikes() - 1))).build());
                }
                recipeRepository.findOne(match(r).where(recipeCondition).set(r.property("numberOfDislikes").to(anonParameter(recipe.getNumberOfDislikes() + 1))).build());
            }
        }
        return recipe;
    }
}
