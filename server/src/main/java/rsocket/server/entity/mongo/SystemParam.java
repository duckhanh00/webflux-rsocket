package rsocket.server.entity.mongo;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "systemparam")
public class SystemParam {

  @Id private String id;
  private Map<String, String> committedVirtualMemorySize;
  private Map<String, String> totalSwapSpaceSize;
  private Map<String, String> freeSwapSpaceSize;
  private Map<String, String> processCpuTime;
  private Map<String, String> freeMemorySize;
  private Map<String, String> totalMemorySize;
  private Map<String, String> cpuLoad;
  private Map<String, String> processCpuLoad;
  private Map<String, String> executionTime;
}
