package bartnik.master.app.graph.recipeforum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecipeRequest {

    @NotBlank
    String title;

    @NotBlank
    String content;

    @NotBlank
    String ingredients;

    @NotBlank
    String tags;

    @NotNull
    UUID category;

}
