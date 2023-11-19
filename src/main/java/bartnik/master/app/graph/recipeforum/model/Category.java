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

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@Label("Category")
@Node("Category")
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    @Relationship(value = "BELONGS_TO_CATEGORY", direction = INCOMING)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<Recipe> recipes = new HashSet<>();
}
