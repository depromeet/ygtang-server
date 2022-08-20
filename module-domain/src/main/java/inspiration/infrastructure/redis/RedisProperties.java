package inspiration.infrastructure.redis;

import lombok.Data;

@Data
public class RedisProperties {
    private String host;
    private int port;
}
