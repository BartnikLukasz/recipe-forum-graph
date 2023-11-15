package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.CommentRepository;
import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.repository.RecipeRepository;
import bartnik.master.app.graph.recipeforum.dto.request.CreateCommentRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateCommentRequest;
import bartnik.master.app.graph.recipeforum.model.Comment;
import bartnik.master.app.graph.recipeforum.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final RecipeRepository recipeRepository;
    private final CustomUserRepository userRepository;
    private final CommentRepository commentRepository;

    public Comment createComment(UUID recipeId, CreateCommentRequest request) {
        var currentUser = UserUtil.getCurrentUser();
        var user = userRepository.getByUsername(currentUser.getUsername());
        var recipe = recipeRepository.getById(recipeId);
        var comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .recipe(recipe)
                .build();

        return commentRepository.save(comment);
    }

    public Comment updateComment(UUID id, UpdateCommentRequest request) {
        var comment = commentRepository.getById(id);
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }

}
