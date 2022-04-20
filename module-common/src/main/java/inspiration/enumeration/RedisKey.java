package inspiration.enumeration;

import lombok.Getter;

@Getter
public enum RedisKey {
    REFRESH("REFRESH_"), EAUTH("EAUTH_");

    private String key;

    RedisKey(String key) {
        this.key = key;
    }
}
