package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.model.CustomUser;
import bartnik.master.app.graph.recipeforum.model.Recipe;
import bartnik.master.app.graph.recipeforum.repository.RecipeRepository;
import bartnik.master.app.graph.recipeforum.util.UserUtil;
import lombok.AllArgsConstructor;
import org.neo4j.driver.internal.value.NodeValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Service
public class UserService {

    private final CustomUserRepository customUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomUser create(CustomUser user) {
        return customUserRepository.save(user);
    }

    public CustomUser addUser() {
        CustomUser user = CustomUser.builder()
                .username("user_admin")
                .password(passwordEncoder.encode("password"))
                .emailAddress("user@example.com")
                .authorities("ROLE_ADMIN")
                .build();
        return create(user);
    }

    public Set<Recipe> getRecommendations(Integer size) {
        var currentUser = UserUtil.getCurrentUser();
        var user = customUserRepository.getByUsername(currentUser.getUsername());
        var recommendations = customUserRepository.getRecommendations(size, user.getId().toString());
        return mapRecommendations(recommendations);
    }

    private Set<Recipe> mapRecommendations(List<NodeValue> recommendations) {
        Set<Recipe> recipes = new HashSet<>();
        recommendations.forEach(nodeValue -> {
            var properties = nodeValue.asMap();
            var recipe = Recipe.builder()
                    .id(UUID.fromString((String) properties.get("id")))
                    .title((String) properties.get("title"))
                    .content((String) properties.get("content"))
                    .ingredients((String) properties.get("ingredients"))
                    .tags((String) properties.get("tags"))
                    .numberOfLikes( ((Long) properties.get("numberOfLikes")).intValue())
                    .numberOfDislikes(((Long) properties.get("numberOfDislikes")).intValue())
                    .created((LocalDate) properties.get("created"))
                    .build();
            recipes.add(recipe);
        });
        return recipes;
    }
}
