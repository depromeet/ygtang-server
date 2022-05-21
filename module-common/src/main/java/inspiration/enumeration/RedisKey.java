package inspiration.enumeration;

import lombok.Getter;

@Getter
public enum RedisKey {
    REFRESH("REFRESH_"),
    EAUTH_SIGN_UP("EAUTH_SIGN_UP"),
    EAUTH_RESET_PASSWORD("EAUTH_RESET_PASSWORD"),
    EAUTH_RESET_PASSWORD_IS_AUTH("TRUE");
    private final String key;

    RedisKey(String key) {
        this.key = key;
    }
}
