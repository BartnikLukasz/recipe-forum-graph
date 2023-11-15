package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.repository.ProductCategoryRepository;
import bartnik.master.app.graph.recipeforum.repository.ProductRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateProductRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateProductRequest;
import bartnik.master.app.graph.recipeforum.model.Product;
import bartnik.master.app.graph.recipeforum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CustomUserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    public Product createProduct(CreateProductRequest request) {
        var productCategory = productCategoryRepository.getById(request.getProductCategory());

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
        return productRepository.getById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(UUID id, UpdateProductRequest request) {
        var product = productRepository.getById(id);
        var productCategory = product.getProductCategory();

        if (!product.getProductCategory().getId().equals(request.getProductCategory())) {
            productCategory = productCategoryRepository.getById(request.getProductCategory());
            product.setProductCategory(productCategory);
        }
        product.apply(request);

        return productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        var product = productRepository.getById(id);

        productRepository.deleteById(id);
    }
}
