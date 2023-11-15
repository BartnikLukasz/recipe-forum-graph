package bartnik.master.app.graph.recipeforum.mapper;

import bartnik.master.app.graph.recipeforum.dto.response.CommentResponse;
import bartnik.master.app.graph.recipeforum.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "username", source = "user.username")
    CommentResponse map(Comment comment);

    @Named("commentList")
    default List<CommentResponse> map(Set<Comment> comments) {
        return comments.stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}
