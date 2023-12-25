package bartnik.master.app.graph.recipeforum.model.projections;

import java.util.UUID;

public interface CustomUserGet {
    UUID getId();

    String getUsername();

    String getAuthorities();

    String getPassword();

}
