package rsocket.server.repository.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import rsocket.server.entity.mongo.Movie;

@Repository
public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {
}
