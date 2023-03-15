package rsocket.server.aspect;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rsocket.client.model.SystemParamDTO;
import rsocket.server.entity.mongo.SystemParam;
import rsocket.server.service.SystemParamService;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

  @Autowired SystemParamService systemParamService;

  @Around("execution(public * rsocket.server.controller.*Controller.*Movie*(..))")
  public Object logInputAndExecutionTime(ProceedingJoinPoint pjp) throws Throwable {

    long t = System.currentTimeMillis();
    String documentId = pjp.getSignature().toShortString();

    log.info("=> {} :: args: {}", pjp.getSignature().toShortString(), pjp.getArgs());
    OperatingSystemMXBean operatingSystemMXBean =
        ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    Object retVal = pjp.proceed();

    //    log.info(
    //        "Lượng virtual memory được đảm bảo có sẵn cho quy trình đang chạy tính bằng byte : {}
    // bytes",
    //        operatingSystemMXBean.getCommittedVirtualMemorySize());
    //    log.info(
    //        "Tổng swap space tính bằng byte: {} bytes",
    // operatingSystemMXBean.getTotalSwapSpaceSize());
    //    log.info(
    //        "Lượng free swap space tính bằng byte: {} bytes",
    //        operatingSystemMXBean.getFreeSwapSpaceSize());
    //    log.info(
    //        "Thời gian CPU được sử dụng bởi process tính bằng nano giây: {} nanoseconds",
    //        operatingSystemMXBean.getProcessCpuTime());
    //    log.info(
    //        "Lượng free physical memory tính bằng byte: {} bytes",
    //        operatingSystemMXBean.getFreeMemorySize());
    //    log.info("Tổng memory tính bằng byte : {}", operatingSystemMXBean.getTotalMemorySize());
    //    log.info(
    //        "Mức sử dụng cpu gần đây cho toàn bộ môi trường hoạt động: {} %",
    //        operatingSystemMXBean.getCpuLoad());
    //    log.info(
    //        "Mức sử dụng cpu gần đây cho process JVM : {} %",
    //        operatingSystemMXBean.getProcessCpuLoad());

    long executionTime = System.currentTimeMillis() - t;


    Map<String, String> committedVirtualMemorySize = new HashMap<>();
    committedVirtualMemorySize.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getCommittedVirtualMemorySize()));

    Map<String, String> totalSwapSpaceSize = new HashMap<>();
    totalSwapSpaceSize.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getTotalSwapSpaceSize()));

    Map<String, String> freeSwapSpaceSize = new HashMap<>();
    freeSwapSpaceSize.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getFreeSwapSpaceSize()));

    Map<String, String> processCpuTime = new HashMap<>();
    processCpuTime.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getProcessCpuTime()));

    Map<String, String> freeMemorySize = new HashMap<>();
    freeMemorySize.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getFreeMemorySize()));

    Map<String, String> totalMemorySize = new HashMap<>();
    totalMemorySize.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getTotalMemorySize()));

    Map<String, String> cpuLoad = new HashMap<>();
    cpuLoad.put(String.valueOf(t), String.valueOf(operatingSystemMXBean.getCpuLoad()));

    Map<String, String> processCpuLoad = new HashMap<>();
    processCpuLoad.put(
        String.valueOf(t), String.valueOf(operatingSystemMXBean.getProcessCpuLoad()));

    Map<String, String> executionTimeMap = new HashMap<>();
    executionTimeMap.put(String.valueOf(t), String.valueOf(executionTime));

    SystemParam systemParam =
        systemParamService
            .updateSystemParam(
                new SystemParamDTO(
                    documentId,
                    committedVirtualMemorySize,
                    totalSwapSpaceSize,
                    freeSwapSpaceSize,
                    processCpuTime,
                    freeMemorySize,
                    totalMemorySize,
                    cpuLoad,
                    processCpuLoad,
                    executionTimeMap))
            .toFuture()
            .get();
    log.info("systemParam: {}", systemParam);
    log.info("<= {} :: Execution Time: {}ms", documentId, executionTime);

    return retVal;
  }
}
