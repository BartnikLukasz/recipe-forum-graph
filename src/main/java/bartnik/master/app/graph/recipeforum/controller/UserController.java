package bartnik.master.app.graph.recipeforum.controller;

import bartnik.master.app.graph.recipeforum.dto.response.RecipeLiteResponse;
import bartnik.master.app.graph.recipeforum.mapper.RecipeMapper;
import bartnik.master.app.graph.recipeforum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/recommendations")
    public ResponseEntity<Set<RecipeLiteResponse>> getRecommendations(@RequestParam("size") Integer size) {
        return ResponseEntity.ok(userService.getRecommendations(size));
    }
}
