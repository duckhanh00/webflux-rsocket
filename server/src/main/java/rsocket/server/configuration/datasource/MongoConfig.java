package rsocket.server.configuration.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@EnableReactiveMongoAuditing
@Configuration
public class MongoConfig {
}