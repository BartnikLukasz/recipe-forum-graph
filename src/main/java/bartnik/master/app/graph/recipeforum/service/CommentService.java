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

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static bartnik.master.app.graph.recipeforum.model.enums.RelationshipTypes.*;
import static org.neo4j.cypherdsl.core.Cypher.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String COMMENT = "Comment";
    private static final String RECIPE = "Recipe";
    private static final String USER = "CustomUser";
    private final RecipeRepository recipeRepository;
    private final CustomUserRepository userRepository;
    private final CommentRepository commentRepository;

    public Comment createComment(UUID recipeId, CreateCommentRequest request) {
        var currentUser = UserUtil.getCurrentUser();
        var u = node(USER).named("u");
        var r = node(RECIPE).named("r");
        var condition = r.property("id").eq(anonParameter(recipeId.toString()));
        var currentUserCondition = u.property("username").eq(anonParameter(currentUser.getUsername()));

        var recipe = recipeRepository.findOne(match(r)
                .where(condition)
                .returning(r)
                .build()).orElseThrow();

        var user = userRepository.findOne(match(u)
                .where(currentUserCondition)
                .returning(u)
                .build()).orElseThrow();

        var comment = Comment.builder()
                .content(request.getContent())
                .created(LocalDate.now())
                .user(user)
                .recipe(recipe)
                .build();

        return commentRepository.save(comment);
    }

    public Comment updateComment(UUID id, UpdateCommentRequest request) {
        var c = node(COMMENT).named("c");
        var condition = c.property("id").eq(anonParameter(id.toString()));
        var updateStatement = match(c)
                .where(condition)
                .set(c.property("content").to(anonParameter(request.getContent())))
                .returning(c)
                .build();
        return commentRepository.findOne(updateStatement).orElseThrow();
    }

    public Set<Comment> getCommentsForRecipe(UUID recipeId) {
        var c = node(COMMENT).named("c");
        var r = node(RECIPE).named("r");
        var u = node(USER).named("u");
        var belongsToRecipe = c.relationshipTo(r, BELONGS_TO_RECIPE.name());
        var addedComments = c.relationshipFrom(u, ADDED_COMMENTS.name());
        var condition = r.property("id").eq(anonParameter(recipeId.toString()));

        var comments = commentRepository.findAll(match(c)
                .match(belongsToRecipe)
                .where(condition)
                .returning(c)
                .build());

        return comments.stream().map( com -> {
            var user = userRepository.findOne(match(c)
                    .match(addedComments)
                    .where(c.property("id").eq(anonParameter(com.getId().toString())))
                    .returning(u)
                    .build()).orElseThrow();
            com.setUser(user);
            return com;
                }).collect(Collectors.toSet());
    }

    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }

}
