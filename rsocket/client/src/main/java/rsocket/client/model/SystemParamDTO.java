package rsocket.client.model;

import java.util.List;
import java.util.Map;

public record SystemParamDTO (
     String id,
     Map<String, String> committedVirtualMemorySize,
     Map<String, String> totalSwapSpaceSize,
     Map<String, String> freeSwapSpaceSize,
     Map<String, String> processCpuTime,
     Map<String, String> freeMemorySize,
     Map<String, String> totalMemorySize,
     Map<String, String> cpuLoad,
     Map<String, String> processCpuLoad,
     Map<String, String> executionTime
){}
