package rsocket.server.repository.mongomvc;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rsocket.server.entity.mongo.Movie;

@Repository
public interface MvcMovieRepository extends MongoRepository<Movie, String> {
}