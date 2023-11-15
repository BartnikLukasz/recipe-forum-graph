package bartnik.master.app.graph.recipeforum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipesFilterRequest extends PageableRequest {

    UUID userId;
    String titleContains;
    String contentContains;
    String ingredientsContains;
    String tagsContains;
    Set<UUID> categoryIds = new HashSet<>();
}
