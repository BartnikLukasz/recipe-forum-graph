package bartnik.master.app.graph.recipeforum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {

    int pageNumber;
    int pageSize;
    String sortBy;
}
