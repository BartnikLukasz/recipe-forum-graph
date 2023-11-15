package bartnik.master.app.graph.recipeforum.mapper;

import bartnik.master.app.graph.recipeforum.dto.response.RecipeDetailsResponse;
import bartnik.master.app.graph.recipeforum.dto.response.RecipeLiteResponse;
import bartnik.master.app.graph.recipeforum.dto.response.RecipeResponse;
import bartnik.master.app.graph.recipeforum.model.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, CommentMapper.class})
public interface RecipeMapper {

    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    RecipeResponse map(Recipe recipe);

    RecipeLiteResponse mapLite(Recipe recipe);

    @Mapping(target = "comments", qualifiedByName = "commentList")
    @Mapping(target = "category", qualifiedByName = "categoryLite")
    RecipeDetailsResponse mapDetails(Recipe recipe);

    default List<RecipeDetailsResponse> mapDetails(List<Recipe> recipes) {
        return recipes.stream()
                .map(this::mapDetails)
                .toList();
    }

    default List<RecipeLiteResponse> mapLite(Set<Recipe> recipes) {
        return recipes.stream()
                .map(this::mapLite)
                .toList();
    }

    default Page<RecipeLiteResponse> mapPage(Page<Recipe> recipe) {
        return recipe.map(this::mapLite);
    }
}
