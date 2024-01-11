package bartnik.master.app.graph.recipeforum.service;

import bartnik.master.app.graph.recipeforum.dto.response.RecipeLiteResponse;
import bartnik.master.app.graph.recipeforum.repository.CustomUserRepository;
import bartnik.master.app.graph.recipeforum.util.UserUtil;
import lombok.AllArgsConstructor;
import org.neo4j.driver.internal.value.NodeValue;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class UserService {

    private final CustomUserRepository customUserRepository;

    public Set<RecipeLiteResponse> getRecommendations(Integer size) {
        var currentUser = UserUtil.getCurrentUser();
        var user = customUserRepository.getByUsernameReadOnly(currentUser.getUsername());

        var recommendations = customUserRepository.getRecommendations(size, user.getId().toString());
        return mapRecommendations(recommendations);
    }

    private Set<RecipeLiteResponse> mapRecommendations(List<NodeValue> recommendations) {
        Set<RecipeLiteResponse> recipes = new HashSet<>();
        recommendations.forEach(nodeValue -> {
            var properties = nodeValue.asMap();
            var recipe = RecipeLiteResponse.builder()
                    .id(UUID.fromString((String) properties.get("id")))
                    .title((String) properties.get("title"))
                    .tags((String) properties.get("tags"))
                    .numberOfLikes( ((Long) properties.get("numberOfLikes")).intValue())
                    .numberOfDislikes(((Long) properties.get("numberOfDislikes")).intValue())
                    .build();
            recipes.add(recipe);
        });

        return recipes;
    }
}
