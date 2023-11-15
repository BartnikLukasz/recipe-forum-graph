package bartnik.master.app.graph.recipeforum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductReportRequest extends OrderReportRequest {

    Set<UUID> productIds = new HashSet<>();
    Set<UUID> excludedProductIds = new HashSet<>();
    Set<UUID> productCategoriesIds = new HashSet<>();
    Set<UUID> excludedProductCategoriesIds = new HashSet<>();
}
