package bartnik.master.app.graph.recipeforum.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class CreateProductCategoryRequest {

    @NotBlank
    String name;

}
