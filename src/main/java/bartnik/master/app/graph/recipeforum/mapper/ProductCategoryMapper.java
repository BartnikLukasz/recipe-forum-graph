package bartnik.master.app.graph.recipeforum.mapper;

import bartnik.master.app.graph.recipeforum.dto.response.ProductCategoryLiteResponse;
import bartnik.master.app.graph.recipeforum.dto.response.ProductCategoryResponse;
import bartnik.master.app.graph.recipeforum.model.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategoryResponse map(ProductCategory category);

    @Named("productCategoryLite")
    ProductCategoryLiteResponse mapLite(ProductCategory category);

    default List<ProductCategoryLiteResponse> map(List<ProductCategory> allProductCategories) {
        return allProductCategories.stream()
                .map(this::mapLite)
                .collect(Collectors.toList());
    }
}
