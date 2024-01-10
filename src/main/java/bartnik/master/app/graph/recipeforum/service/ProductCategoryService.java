package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.ProductCategoryRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateProductCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateProductCategoryRequest;
import bartnik.master.app.graph.recipeforum.model.ProductCategory;
import bartnik.master.app.graph.recipeforum.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.BELONGS_TO_CATEGORY;
import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.BELONGS_TO_PRODUCT_CATEGORY;
import static org.neo4j.cypherdsl.core.Cypher.*;
import static org.neo4j.cypherdsl.core.Cypher.anonParameter;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private static final String PRODUCT_CATEGORY = "ProductCategory";
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    public ProductCategory createProductCategory(CreateProductCategoryRequest request) {
        var productCategory = ProductCategory.builder()
                .name(request.getName())
                .build();

        return productCategoryRepository.save(productCategory);
    }

    public ProductCategory updateProductCategory(UUID id, UpdateProductCategoryRequest request) {
        var pc = node(PRODUCT_CATEGORY).named("pc");
        var condition = pc.property("id").eq(anonParameter(id.toString()));
        var updateStatement = match(pc)
                .where(condition)
                .set(pc.property("name").to(anonParameter(request.getName())))
                .returning(pc)
                .build();
        return productCategoryRepository.findOne(updateStatement).orElseThrow();
    }

    public void deleteProductCategory(UUID id) {
        productCategoryRepository.deleteById(id);
    }

    public ProductCategory getProductsForProductCategory(UUID id) {
        var pc = node(PRODUCT_CATEGORY).named("pc");
        var p = node("Product").named("p");
        var belongsToCategory = p.relationshipTo(pc, BELONGS_TO_PRODUCT_CATEGORY.name());
        var condition = pc.property("id").eq(anonParameter(id.toString()));
        var findStatement = match(pc)
                .where(condition)
                .returning(pc)
                .build();
        var findRecipesStatement = match(pc)
                .where(condition)
                .match(belongsToCategory)
                .returning(p)
                .build();
        var category = productCategoryRepository.findOne(findStatement).orElseThrow();
        var products = productRepository.findAll(findRecipesStatement);
        category.setProducts(new HashSet<>(products));
        return category;
    }

    public List<ProductCategory> getAllProductCategories() {
        var pc = node(PRODUCT_CATEGORY).named("pc");
        var findStatement = match(pc).returning(pc).build();
        return productCategoryRepository.findAll(findStatement).stream().toList();
    }
}
