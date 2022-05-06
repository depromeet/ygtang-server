package inspiration.enumeration;

import lombok.Getter;

@Getter
public enum RedisKey {
    REFRESH("REFRESH_"),
    EAUTH_SIGN_UP("EAUTH_SIGN_UP"),
    EAUTH_UPDATE_PASSWORD("EAUTH_UPDATE_PASSWORD");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }
}
