package bartnik.master.app.graph.recipeforum.controller;

import bartnik.master.app.graph.recipeforum.dto.request.CreateCommentRequest;
import bartnik.master.app.graph.recipeforum.dto.request.UpdateCommentRequest;
import bartnik.master.app.graph.recipeforum.dto.response.CommentResponse;
import bartnik.master.app.graph.recipeforum.mapper.CommentMapper;
import bartnik.master.app.graph.recipeforum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper mapper;

    @PostMapping("/{recipeId}")
    public ResponseEntity<CommentResponse> createComment(@PathVariable UUID recipeId, @RequestBody @Validated CreateCommentRequest request) {
        return ResponseEntity.ok(mapper.map(commentService.createComment(recipeId, request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable UUID id, @RequestBody @Validated UpdateCommentRequest request) {
        return ResponseEntity.ok(mapper.map(commentService.updateComment(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentResponse> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
