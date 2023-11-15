package bartnik.master.app.graph.recipeforum.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Validated
public class RecipeLiteResponse {

    @NotNull
    UUID id;

    @NotBlank
    String title;

    @NotBlank
    String tags;

    Integer numberOfLikes;

    Integer numberOfDislikes;
}
