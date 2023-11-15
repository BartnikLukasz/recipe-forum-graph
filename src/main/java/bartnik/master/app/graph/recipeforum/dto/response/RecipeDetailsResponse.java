package bartnik.master.app.graph.recipeforum.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Validated
public class RecipeDetailsResponse extends RecipeResponse {

    @NotNull
    CategoryLiteResponse category;

    @Builder.Default
    List<CommentResponse> comments = new ArrayList<>();

    @NotNull
    UserResponse user;
}
