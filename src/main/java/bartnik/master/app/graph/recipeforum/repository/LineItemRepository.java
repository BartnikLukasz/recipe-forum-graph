package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.LineItem;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LineItemRepository extends Neo4jRepository<LineItem, UUID>, CypherdslStatementExecutor<LineItem> {

    default LineItem getById(UUID id){
        return findById(id).orElseThrow();
    }
}
