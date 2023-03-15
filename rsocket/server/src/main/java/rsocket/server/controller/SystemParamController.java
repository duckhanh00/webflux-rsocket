package rsocket.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.model.SystemParamDTO;
import rsocket.server.entity.mongo.SystemParam;
import rsocket.server.service.SystemParamService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SystemParamController {

  private final SystemParamService systemParamService;

  @GetMapping(value = "/system-param", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<SystemParam> getSystemParams() {
    return systemParamService.getSystemParams();
  }

  @PostMapping(value = "/system-param", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Mono<SystemParam> createSystemParams(@Valid @RequestBody SystemParamDTO systemParamDTO) {
    return systemParamService.createSystemParam(systemParamDTO);
  }
}
