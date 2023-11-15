package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.repository.ProductCategoryRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateProductCategoryRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateProductCategoryRequest;
import bartnik.master.app.graph.recipeforum.model.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final CustomUserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategory createProductCategory(CreateProductCategoryRequest request) {
        var productCategory = ProductCategory.builder()
                .name(request.getName())
                .build();

        return productCategoryRepository.save(productCategory);
    }

    public ProductCategory updateProductCategory(UUID id, UpdateProductCategoryRequest request) {
        var productCategory = productCategoryRepository.getById(id);
        productCategory.setName(request.getName());
        return productCategoryRepository.save(productCategory);
    }

    public void deleteProductCategory(UUID id) {
        productCategoryRepository.deleteById(id);
    }

    public ProductCategory getProductsForProductCategory(UUID id) {
        return productCategoryRepository.getById(id);
    }

    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepository.findAll();
    }
}
