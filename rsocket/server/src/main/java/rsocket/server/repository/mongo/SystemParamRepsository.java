package rsocket.server.repository.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import rsocket.server.entity.mongo.SystemParam;

@Repository
public interface SystemParamRepsository extends ReactiveMongoRepository<SystemParam, String> {}
