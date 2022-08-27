package inspiration.auth;

import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class TokenResponseVo {
    String accessToken;
    String refreshToken;
    Long accessTokenExpireDate;
    long memberId;
}
