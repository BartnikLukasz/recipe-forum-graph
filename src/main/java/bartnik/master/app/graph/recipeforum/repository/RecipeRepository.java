package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.Category;
import bartnik.master.app.graph.recipeforum.model.Recipe;
import org.neo4j.cypherdsl.core.*;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.DISLIKED;
import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.LIKED;
import static org.neo4j.cypherdsl.core.Cypher.literalOf;
import static org.neo4j.cypherdsl.core.Cypher.match;

@Repository
public interface RecipeRepository extends Neo4jRepository<Recipe, UUID>, CypherdslStatementExecutor<Recipe> {

    default boolean isReactedByUser(UUID recipeId, UUID userId, boolean liked) {

        var recipe = Cypher.node("Recipe").named("r");
        var user = Cypher.node("CustomUser").named("u");
        var relationship = liked ? LIKED.name() : DISLIKED.name();

        var condition = user.property("id").isEqualTo(literalOf(userId.toString()))
                    .and(recipe.property("id").isEqualTo(literalOf(recipeId.toString())));
        var query = match(user.relationshipTo(recipe, relationship))
                .where(condition)
                .returning(recipe.property("id"))
                .build();
        return findOne(query).isPresent();
    }

    default Recipe getById(UUID id){
        return findById(id).orElseThrow();
    }

}
