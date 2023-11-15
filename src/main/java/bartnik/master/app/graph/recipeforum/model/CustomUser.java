package bartnik.master.app.graph.recipeforum.model;

import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@Label("CustomUser")
@Node("CustomUser")
@AllArgsConstructor
public class CustomUser {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String username;

    @NotBlank
    @EqualsAndHashCode.Exclude
    private String password;

    private String emailAddress;

    private String authorities;

    @Relationship(value = "ADDED", direction = OUTGOING)
    @EqualsAndHashCode.Exclude
    private Set<Recipe> recipes;

    @Relationship(value = "ADDED_COMMENTS", direction = OUTGOING)
    @EqualsAndHashCode.Exclude
    private List<Comment> comments;

    @Relationship(value = "ORDERED", direction = OUTGOING)
    @EqualsAndHashCode.Exclude
    private List<Order> orders;

    @Relationship(value = "LIKED", direction = OUTGOING)
    @EqualsAndHashCode.Exclude
    private Set<Recipe> likedRecipes;

    @Relationship(value = "DISLIKED", direction = OUTGOING)
    @EqualsAndHashCode.Exclude
    private Set<Recipe> dislikedRecipes;
}
