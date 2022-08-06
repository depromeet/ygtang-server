package inspiration.enumeration;

import lombok.Getter;

@Getter
public final class ExpireTimeConstants {

    public static final Long expireSingUpAccessTokenTime = 60 * 5L;

    public static final Long expireUpdatePasswordAccessTokenTime = 60 * 30L;

    public static final Long accessTokenValidMillisecond = 60 * 60 * 1000L;

    public static final Long refreshTokenValidMillisecond = 14 * 24 * 60 * 60 * 1000L;

}
