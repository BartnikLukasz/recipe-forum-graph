package bartnik.master.app.graph.recipeforum.model;

import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@Label("Order")
@Node("Order")
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Relationship(value = "ORDERED", direction = INCOMING)
    private CustomUser user;

    @Relationship(value = "BELONGS_TO_ORDER", direction = INCOMING)
    private List<LineItem> items;

    private BigDecimal value;

    private LocalDateTime orderDate;
}
