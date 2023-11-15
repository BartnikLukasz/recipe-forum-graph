package bartnik.master.app.graph.recipeforum.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class OrderProductsRequest {

    @NotEmpty
    List<LineItemRequest> lineItems;
}
