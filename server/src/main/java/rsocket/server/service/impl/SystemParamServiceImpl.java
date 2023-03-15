package rsocket.server.service.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.exception.MovieNotFoundException;
import rsocket.client.model.SystemParamDTO;
import rsocket.server.entity.mongo.SystemParam;
import rsocket.server.mapper.SystemParamMapper;
import rsocket.server.repository.mongo.SystemParamRepsository;
import rsocket.server.service.SystemParamService;

@RequiredArgsConstructor
@Service
public class SystemParamServiceImpl implements SystemParamService {

  private final SystemParamMapper systemParamMapper;
  private final SystemParamRepsository systemParamRepsository;

  @Override
  public Flux<SystemParam> getSystemParams() {
    return systemParamRepsository.findAll();
  }

  @Override
  public Mono<SystemParam> getSystemParam(String id) {
    return systemParamRepsository
        .findById(id)
        .switchIfEmpty(Mono.error(new MovieNotFoundException(id)));
  }

  @Override
  public Mono<SystemParam> createSystemParam(SystemParamDTO systemParamDTO) {
    return systemParamRepsository.save(systemParamMapper.toSystemParam(systemParamDTO));
  }

  @Override
  public Mono<SystemParam> updateSystemParam(SystemParamDTO systemParamDTO) {
    return systemParamRepsository
        .findById(systemParamDTO.id())
        .flatMap(
            item -> {
              Map.Entry<String, String> entryCommitted =
                  systemParamDTO.committedVirtualMemorySize().entrySet().iterator().next();
              Map.Entry<String, String> entryTotalSwapSpaceSize =
                  systemParamDTO.totalSwapSpaceSize().entrySet().iterator().next();
              Map.Entry<String, String> entryFreeSwapSpaceSize =
                  systemParamDTO.freeSwapSpaceSize().entrySet().iterator().next();
              Map.Entry<String, String> entryProcessCpuLoad =
                  systemParamDTO.processCpuLoad().entrySet().iterator().next();
              Map.Entry<String, String> entryProcessCpuTime =
                  systemParamDTO.processCpuTime().entrySet().iterator().next();
              Map.Entry<String, String> entryCpuLoad =
                  systemParamDTO.cpuLoad().entrySet().iterator().next();
              Map.Entry<String, String> entryFreeMemorySize =
                  systemParamDTO.freeMemorySize().entrySet().iterator().next();
              Map.Entry<String, String> entryTotalMemorySize =
                  systemParamDTO.totalMemorySize().entrySet().iterator().next();
              Map.Entry<String, String> entryExecutionTime =
                  systemParamDTO.executionTime().entrySet().iterator().next();

              item.getCommittedVirtualMemorySize()
                  .put(
                      entryCommitted.getKey(),
                      systemParamDTO.committedVirtualMemorySize().get(entryCommitted.getKey()));
              item.getTotalSwapSpaceSize()
                  .put(
                      entryTotalSwapSpaceSize.getKey(),
                      systemParamDTO.totalSwapSpaceSize().get(entryTotalSwapSpaceSize.getKey()));
              item.getFreeSwapSpaceSize()
                  .put(
                      entryFreeSwapSpaceSize.getKey(),
                      systemParamDTO.freeSwapSpaceSize().get(entryFreeSwapSpaceSize.getKey()));
              item.getProcessCpuLoad()
                  .put(
                      entryProcessCpuLoad.getKey(),
                      systemParamDTO.processCpuLoad().get(entryProcessCpuLoad.getKey()));
              item.getProcessCpuTime()
                  .put(
                      entryProcessCpuTime.getKey(),
                      systemParamDTO.processCpuTime().get(entryProcessCpuTime.getKey()));
              item.getCpuLoad()
                  .put(entryCpuLoad.getKey(), systemParamDTO.cpuLoad().get(entryCpuLoad.getKey()));
              item.getFreeMemorySize()
                  .put(
                      entryFreeMemorySize.getKey(),
                      systemParamDTO.freeMemorySize().get(entryFreeMemorySize.getKey()));
              item.getTotalMemorySize()
                  .put(
                      entryTotalMemorySize.getKey(),
                      systemParamDTO.totalMemorySize().get(entryTotalMemorySize.getKey()));
              item.getExecutionTime()
                  .put(
                      entryExecutionTime.getKey(),
                      systemParamDTO.executionTime().get(entryExecutionTime.getKey()));

              return systemParamRepsository.save(item);
            })
        .switchIfEmpty(
            systemParamRepsository.save(systemParamMapper.toSystemParam(systemParamDTO)));
  }
}
