package bartnik.master.app.graph.recipeforum.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    @NotNull
    UUID id;

    @NotNull
    String username;

    @NotNull
    BigDecimal value;

    @NotEmpty
    List<LineItemResponse> items;
}