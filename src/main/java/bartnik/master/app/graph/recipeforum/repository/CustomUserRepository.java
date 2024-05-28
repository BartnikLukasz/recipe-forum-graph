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

    @Query("MATCH (u:`CustomUser`{id: $userId})-[:`LIKED`]->(r:`Recipe`)\n" +
            "WITH u, collect(r) as likedRecipes\n" +
            "MATCH (u)-[:`LIKED`]->(:`Recipe`)<-[:`LIKED`]-(peer:`CustomUser`)-[:`LIKED`]->(reco:`Recipe`)\n" +
            "WHERE NOT reco IN likedRecipes\n" +
            "WITH reco, count(*) as Frequency\n" +
            "RETURN reco ORDER BY Frequency DESC\n" +
            "LIMIT $size;")
    List<NodeValue> getRecommendations(@Param("size") Integer size, @Param("userId") String userId);
}
