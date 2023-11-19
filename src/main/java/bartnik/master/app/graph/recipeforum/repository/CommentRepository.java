package bartnik.master.app.graph.recipeforum.repository;

import bartnik.master.app.graph.recipeforum.model.Comment;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends Neo4jRepository<Comment, UUID> {

    default Comment getById(UUID id){
        return findById(id).orElseThrow();
    }
}
