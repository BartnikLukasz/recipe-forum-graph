package bartnik.master.app.graph.recipeforum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportResponse {

    List<ProductsGroupedResponse> products = new ArrayList<>();

    BigDecimal value;

    LocalDate from;

    LocalDate to;
}
