package bartnik.master.app.graph.recipeforum.model;

import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Label;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Label("Comment")
@Node("Comment")
public class Comment {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String content;

    @Builder.Default
    private LocalDate created = LocalDate.now();

    @Relationship(value = "ADDED_COMMENTS", direction = INCOMING)
    private CustomUser user;

    @Relationship(value = "BELONGS_TO_RECIPE", direction = OUTGOING)
    private Recipe recipe;
}
