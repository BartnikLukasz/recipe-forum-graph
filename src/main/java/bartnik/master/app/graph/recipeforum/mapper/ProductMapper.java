package bartnik.master.app.graph.recipeforum.mapper;

import bartnik.master.app.graph.recipeforum.dto.response.ProductResponse;
import bartnik.master.app.graph.recipeforum.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface ProductMapper {

    ProductResponse map(Product product);

    default List<ProductResponse> map(List<Product> products) {
        return products.stream()
                .map(this::map)
                .toList();
    }
}
