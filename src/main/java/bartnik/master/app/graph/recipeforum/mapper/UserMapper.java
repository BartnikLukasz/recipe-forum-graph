package bartnik.master.app.graph.recipeforum.mapper;

import bartnik.master.app.graph.recipeforum.dto.response.UserResponse;
import bartnik.master.app.graph.recipeforum.model.CustomUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse map(CustomUser user);
}
