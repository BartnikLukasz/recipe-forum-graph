package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.ProductCategory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends Neo4jRepository<ProductCategory, UUID>, CypherdslStatementExecutor<ProductCategory> {

    default ProductCategory getById(UUID id){
        return findById(id).orElseThrow();
    }
}
