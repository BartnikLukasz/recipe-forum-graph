package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.Category;
import bartnik.master.app.graph.recipeforum.model.Order;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends Neo4jRepository<Order, UUID>, CypherdslStatementExecutor<Order> {

    default Order getById(UUID id){
        return findById(id).orElseThrow();
    }
}
