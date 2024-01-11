package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.CustomUser;
import bartnik.master.app.graph.recipeforum.model.projections.CustomUserGet;
import org.neo4j.driver.internal.value.NodeValue;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CustomUserRepository extends Neo4jRepository<CustomUser, UUID>, CypherdslStatementExecutor<CustomUser> {

    Optional<CustomUserGet> findReadOnlyByUsername(String username);

    default CustomUserGet getByUsernameReadOnly(String username) {
        return findReadOnlyByUsername(username).orElseThrow();
    }

    @Query("MATCH (u:`CustomUser` {id: $userId})-[:`LIKED`]->(r:`Recipe`)<-[:`ADDED`]-(cu:`CustomUser`)\n" +
            "WITH cu, u, count(*) AS RecipeCount\n" +
            "ORDER BY RecipeCount DESC\n" +
            "LIMIT 5\n" +
            "WITH collect(cu.id) AS TopUsers, u\n" +
            "MATCH (u)-[:`LIKED`]->(r:`Recipe`)-[:`BELONGS_TO_CATEGORY`]->(c:`Category`)\n" +
            "WITH c, count(*) AS RecipeCount, TopUsers, u\n" +
            "ORDER BY RecipeCount DESC\n" +
            "LIMIT 3\n" +
            "WITH collect(c.id) AS TopCategories, TopUsers, u\n" +
            "MATCH (u)-[:`LIKED`]->(r:`Recipe`)<-[:`LIKED`]-(peer:`CustomUser`)-[:`LIKED`]->(reco:`Recipe`)<-[:`ADDED`]-(adder:`CustomUser`), (reco)-[:`BELONGS_TO_CATEGORY`]->(cat:`Category`)\n" +
            "WHERE NOT (u)-[:`LIKED`]->(reco)\n" +
            "WITH reco, adder, cat, count(*) * " +
            "            CASE WHEN adder.id IN TopUsers THEN 2 ELSE 1 END *" +
            "            CASE WHEN cat.id IN TopCategories THEN 2 ELSE 1 END AS AdjustedFrequency, TopUsers, TopCategories\n" +
            "RETURN reco " +
            "ORDER BY AdjustedFrequency DESC\n" +
            "LIMIT $size;")
    List<NodeValue> getRecommendations(@Param("size") Integer size, @Param("userId") String userId);
}
