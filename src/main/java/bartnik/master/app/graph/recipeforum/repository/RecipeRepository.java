package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.Recipe;
import bartnik.master.app.graph.recipeforum.model.projections.RecipeLiteGet;
import org.neo4j.cypherdsl.core.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.DISLIKED;
import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.LIKED;
import static org.neo4j.cypherdsl.core.Cypher.*;

@Repository
public interface RecipeRepository extends Neo4jRepository<Recipe, UUID>, CypherdslStatementExecutor<Recipe> {

    default boolean isReactedByUser(UUID recipeId, UUID userId, boolean liked) {

        var recipe = node("Recipe").named("r");
        var user = node("CustomUser").named("u");
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

    @Query(value = "MATCH (r:Recipe) " +
            "OPTIONAL MATCH (r)-[:BELONGS_TO_CATEGORY]->(c:Category) " +
            "WHERE (size($categoryIds) = 0 OR c.id IN $categoryIds)" +
            "OPTIONAL MATCH (r)-[:ADDED]->(u:CustomUser) " +
            "WHERE ($userId IS NULL OR u.id = $userId) " +
            "WITH r " +
            "WHERE ($titleContains IS NULL OR r.title CONTAINS $titleContains) " +
            "AND ($contentContains IS NULL OR r.content CONTAINS $contentContains) " +
            "AND ($ingredientsContains IS NULL OR r.ingredients CONTAINS $ingredientsContains) " +
            "AND ($tagsContains IS NULL OR r.tags CONTAINS $tagsContains) " +
            "AND (size($categoryIds) = 0 OR c.id IN $categoryIds) " +
            "RETURN r " +
            "ORDER BY $sortProperty, elementId(r) ASC " +
            "SKIP $skip LIMIT $limit",
            countQuery = "MATCH (r:Recipe) " +
            "OPTIONAL MATCH (r)-[:BELONGS_TO_CATEGORY]->(c:Category) " +
            "WHERE (size($categoryIds) = 0 OR c.id IN $categoryIds)" +
            "OPTIONAL MATCH (r)-[:ADDED]->(u:CustomUser) " +
            "WHERE ($userId IS NULL OR u.id = $userId) " +
            "WITH r " +
            "WHERE ($titleContains IS NULL OR r.title CONTAINS $titleContains) " +
            "AND ($contentContains IS NULL OR r.content CONTAINS $contentContains) " +
            "AND ($ingredientsContains IS NULL OR r.ingredients CONTAINS $ingredientsContains) " +
            "AND ($tagsContains IS NULL OR r.tags CONTAINS $tagsContains) " +
            "AND (size($categoryIds) = 0 OR c.id IN $categoryIds) " +
            "RETURN count(r)")
    Page<RecipeLiteGet> findAllFiltered(@Param("userId") UUID userId,
                                        @Param("titleContains") String titleContains,
                                        @Param("contentContains") String contentContains,
                                        @Param("ingredientsContains") String ingredientsContains,
                                        @Param("tagsContains") String tagsContains,
                                        @Param("categoryIds") Set<UUID> categoryIds,
                                        @Param("sortProperty") String sortProperty,
                                        @Param("skip") int skip,
                                        @Param("limit") int limit,
                                        Pageable pageable);

}
