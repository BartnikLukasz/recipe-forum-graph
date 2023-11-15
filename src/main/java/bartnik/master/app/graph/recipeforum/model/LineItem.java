package bartnik.master.app.graph.recipeforum.model;

import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@Label("LineItem")
@Node("LineItem")
@AllArgsConstructor
public class LineItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Relationship(value = "BELONGS_TO_ITEM", direction = INCOMING)
    private Product product;

    @Relationship(value = "BELONGS_TO_ORDER", direction = OUTGOING)
    private Order order;

    private Integer quantity;
}
