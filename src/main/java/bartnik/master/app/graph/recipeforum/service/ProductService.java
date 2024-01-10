package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.ProductCategoryRepository;
import bartnik.master.app.graph.recipeforum.repository.ProductRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateProductRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateProductRequest;
import bartnik.master.app.graph.recipeforum.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.BELONGS_TO_PRODUCT_CATEGORY;
import static org.neo4j.cypherdsl.core.Cypher.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String PRODUCT_CATEGORY = "ProductCategory";
    private static final String PRODUCT = "Product";
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    public Product createProduct(CreateProductRequest request) {
        var pc = node(PRODUCT_CATEGORY).named("pc");

        var productCategory = productCategoryRepository.findOne(match(pc)
                .where(pc.property("id").eq(anonParameter(request.getProductCategory().toString())))
                .returning(pc)
                .build()).orElseThrow();

        var product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .availability(request.getAvailability())
                .productCategory(productCategory)
                .build();

        return productRepository.save(product);
    }

    public Product getProductById(UUID id) {
        var p = node(PRODUCT).named("p");
        return productRepository.findOne(match(p).where(p.property("id").eq(anonParameter(id.toString()))).returning(p).build()).orElseThrow();
    }

    public List<Product> getAllProducts() {
        var p = node(PRODUCT).named("p");
        return productRepository.findAll(match(p).returning(p).build()).stream().toList();
    }

    @Transactional
    public Product updateProduct(UUID id, UpdateProductRequest request) {
        var p = node(PRODUCT).named("p");
        var pc = node(PRODUCT_CATEGORY).named("pc");
        var belongsToProductCategory = pc.relationshipFrom(p, BELONGS_TO_PRODUCT_CATEGORY.name());
        var condition = p.property("id").eq(anonParameter(id.toString()));

        var product = productRepository.findOne(match(p)
                .where(condition)
                .returning(p)
                .build()).orElseThrow();

        var productCategory = productCategoryRepository.findOne(match(p)
                .where(condition)
                .match(belongsToProductCategory)
                .returning(pc)
                .build()).orElseThrow();

        if (!productCategory.getId().equals(request.getProductCategory())) {
            var cOld = node(PRODUCT_CATEGORY).named("cOld");
            var cNew = node(PRODUCT_CATEGORY).named("cNew");

            var belongsToOldProductCategory = cOld.relationshipFrom(p, BELONGS_TO_PRODUCT_CATEGORY.name());
            var belongsToNewProductCategory = cNew.relationshipFrom(p, BELONGS_TO_PRODUCT_CATEGORY.name());

            var statement = match(belongsToOldProductCategory)
                    .where(cOld.property("id").eq(anonParameter(productCategory.getId().toString())))
                    .and(p.property("id").eq(anonParameter(id.toString())))
                    .match(cNew)
                    .where(cNew.property("id").eq(anonParameter(request.getProductCategory().toString())))
                    .create(belongsToNewProductCategory)
                    .delete(belongsToOldProductCategory)
                    .returning(cNew)
                    .build();

            productCategory = productCategoryRepository.findOne(statement).orElseThrow();
        }

        var updateStatement = match(p)
                .where(condition)
                .set(p.property("name").to(anonParameter(request.getName())))
                .set(p.property("description").to(anonParameter(request.getDescription())))
                .set(p.property("availability").to(anonParameter(request.getAvailability())))
                .set(p.property("price").to(anonParameter(request.getPrice().toString())))
                .returning(p)
                .build();

        product = productRepository.findOne(updateStatement).orElseThrow();
        product.setProductCategory(productCategory);

        return product;
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
