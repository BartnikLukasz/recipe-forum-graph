package bartnik.master.app.graph.recipeforum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportRequest {

    Set<UUID> userIds = new HashSet<>();
    Set<UUID> excludedUserIds = new HashSet<>();
    LocalDate from;
    LocalDate to;
}
