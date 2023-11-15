package bartnik.master.app.graph.recipeforum.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Validated
public class CategoryResponse extends CategoryLiteResponse {

    @Builder.Default
    Set<RecipeResponse> recipes = new HashSet<>();
}
