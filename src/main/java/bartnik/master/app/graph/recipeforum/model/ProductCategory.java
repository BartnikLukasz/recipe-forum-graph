package bartnik.master.app.graph.recipeforum.model;

import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Label("ProductCategory")
@Node("ProductCategory")
public class ProductCategory {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    @Relationship(value = "BELONGS_TO_PRODUCT_CATEGORY", direction = INCOMING)
    private Set<Product> products = new HashSet<>();
}
