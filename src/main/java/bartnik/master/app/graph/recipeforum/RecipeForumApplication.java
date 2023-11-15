package bartnik.master.app.graph.recipeforum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;

@EnableNeo4jRepositories
@SpringBootApplication
public class RecipeForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeForumApplication.class, args);
	}

}
