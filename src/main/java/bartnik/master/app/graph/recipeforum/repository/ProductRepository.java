package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.Product;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends Neo4jRepository<Product, UUID>, CypherdslStatementExecutor<Product> {

    default Product getById(UUID id){
        return findById(id).orElseThrow();
    }
}
