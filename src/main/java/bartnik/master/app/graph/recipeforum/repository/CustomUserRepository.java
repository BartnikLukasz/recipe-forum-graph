package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.CustomUser;
import bartnik.master.app.graph.recipeforum.model.Recipe;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Functions;
import org.neo4j.driver.internal.value.NodeValue;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static org.neo4j.cypherdsl.core.Conditions.not;
import static org.neo4j.cypherdsl.core.Cypher.match;
import static org.neo4j.cypherdsl.core.Cypher.node;

@Repository
public interface CustomUserRepository extends Neo4jRepository<CustomUser, UUID>, CypherdslStatementExecutor<Recipe> {
    Optional<CustomUser> findByUsername(String username);
    default CustomUser getByUsername(String username) {
        return findByUsername(username).orElseThrow();
    }

    default CustomUser getById(UUID id){
        return findById(id).orElseThrow();
    }

    @Query("MATCH (u:`CustomUser`)-[:`LIKED`]->(r:`Recipe`)<-[:`LIKED`]-(peer:`CustomUser`)-[:`LIKED`]->(reco:`Recipe`) " +
            "WHERE (u.id = $userId AND NOT (u)-[:`LIKED`]->(reco)) " +
            "WITH reco, count(*) AS Frequency RETURN reco ORDER BY Frequency DESC LIMIT $size")
    List<NodeValue> getRecommendations(@Param("size") Integer size, @Param("userId") String userId);
}
