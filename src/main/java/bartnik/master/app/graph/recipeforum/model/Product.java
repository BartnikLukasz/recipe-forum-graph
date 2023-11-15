package bartnik.master.app.graph.recipeforum.model;

import bartnik.master.app.graph.recipeforum.dto.request.UpdateProductRequest;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Label("Product")
@Node("Product")
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    private String description;

    private BigDecimal price;

    @Builder.Default
    private Integer availability = 0;

    @Relationship(value = "BELONGS_TO_ITEM", direction = OUTGOING)
    private List<LineItem> lineItems;

    @Relationship(value = "BELONGS_TO_PRODUCT_CATEGORY", direction = OUTGOING)
    private ProductCategory productCategory;

    public void apply(UpdateProductRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.availability = request.getAvailability();
        this.price = request.getPrice();
    }

}
