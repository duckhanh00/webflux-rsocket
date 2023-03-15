package rsocket.server.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.model.SystemParamDTO;
import rsocket.server.entity.mongo.SystemParam;

public interface SystemParamService {

  Flux<SystemParam> getSystemParams();

  Mono<SystemParam> getSystemParam(String id);

  Mono<SystemParam> createSystemParam(SystemParamDTO systemParamDTO);

  Mono<SystemParam> updateSystemParam(SystemParamDTO systemParamDTO);
}
