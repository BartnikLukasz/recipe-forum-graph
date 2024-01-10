package bartnik.master.app.graph.recipeforum.model.projections;

import java.util.UUID;

public interface RecipeLiteGet {

    UUID getId();

    String getTitle();

    String getTags();

    Integer getNumberOfLikes();

    Integer getNumberOfDislikes();
}
