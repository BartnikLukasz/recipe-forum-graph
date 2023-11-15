package bartnik.master.app.graph.recipeforum.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReportResponse {

    List<OrderResponse> orders;

    BigDecimal value;

    LocalDate from;

    LocalDate to;
}
