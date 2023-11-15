package bartnik.master.app.graph.recipeforum.controller;

import bartnik.master.app.graph.recipeforum.dto.request.CreateRecipeRequest;
import bartnik.master.app.graph.recipeforum.dto.request.RecipesFilterRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateRecipeRequest;
import bartnik.master.app.graph.recipeforum.dto.response.RecipeDetailsResponse;
import bartnik.master.app.graph.recipeforum.dto.response.RecipeLiteResponse;
import bartnik.master.app.graph.recipeforum.dto.response.RecipeResponse;
import bartnik.master.app.graph.recipeforum.mapper.RecipeMapper;
import bartnik.master.app.graph.recipeforum.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody  @Validated CreateRecipeRequest request) {
        return ResponseEntity.ok(recipeMapper.map(recipeService.createRecipe(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailsResponse> getRecipe(@PathVariable UUID id) {
        return ResponseEntity.ok(recipeMapper.mapDetails(recipeService.getRecipeById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable UUID id, @RequestBody @Validated UpdateRecipeRequest request) {
        return ResponseEntity.ok(recipeMapper.map(recipeService.updateRecipe(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RecipeResponse> deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/all")
    public ResponseEntity<Page<RecipeLiteResponse>> getAllRecipes(@RequestBody @Validated RecipesFilterRequest filter) {
        return ResponseEntity.ok(recipeMapper.mapPage(recipeService.findRecipes(filter)));
    }

    @PutMapping("/{id}/rate")
    public ResponseEntity<RecipeResponse> rateRecipe(@RequestParam("liked") boolean liked, @PathVariable UUID id) {
        return ResponseEntity.ok(recipeMapper.map(recipeService.rateRecipe(id, liked)));
    }
}
