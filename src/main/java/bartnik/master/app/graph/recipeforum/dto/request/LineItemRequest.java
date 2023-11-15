package bartnik.master.app.graph.recipeforum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class LineItemRequest {

    @NotNull
    UUID productId;

    @NotNull
    Integer quantity;
}
