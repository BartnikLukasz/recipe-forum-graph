package bartnik.master.app.graph.recipeforum.config;

import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.ogm.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.Neo4jClient;

@org.springframework.context.annotation.Configuration
public class Neo4jConfig {
    @Bean
    Configuration cypherDslConfiguration() {
        return Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
    }

//    @Bean
//    public Neo4jClient neo4jClient(org.neo4j.driver.Driver driver) {
//        return Neo4jClient.create(new Driver());
//    }
}
