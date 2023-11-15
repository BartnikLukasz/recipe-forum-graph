package bartnik.master.app.graph.recipeforum.model;

import bartnik.master.app.graph.recipeforum.dto.request.UpdateRecipeRequest;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Label("Recipe")
@Node("Recipe")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String ingredients;

    @NotBlank
    private String tags;

    @Builder.Default
    Integer numberOfLikes = 0;

    @Builder.Default
    Integer numberOfDislikes = 0;

    @Builder.Default
    private LocalDate created = LocalDate.now();

    @Relationship(value = "BELONGS_TO_CATEGORY", direction = OUTGOING)
    @EqualsAndHashCode.Exclude
    private Category category;

    @Relationship(value = "ADDED", direction = INCOMING)
    @EqualsAndHashCode.Exclude
    private CustomUser user;

    @Relationship(value = "BELONGS_TO_RECIPE", direction = INCOMING)
    @EqualsAndHashCode.Exclude
    private Set<Comment> comments;

    @Relationship(value = "LIKED", direction = INCOMING)
    @EqualsAndHashCode.Exclude
    private Set<CustomUser> likedByUsers = new HashSet<CustomUser>();

    @Relationship(value = "DISLIKED", direction = INCOMING)
    @EqualsAndHashCode.Exclude
    private Set<CustomUser> dislikedByUsers = new HashSet<CustomUser>();

    public void apply(UpdateRecipeRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.ingredients = request.getIngredients();
        this.tags = request.getTags();
    }
}
