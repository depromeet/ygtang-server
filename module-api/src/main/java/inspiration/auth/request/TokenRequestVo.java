package inspiration.auth.request;

import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class TokenRequestVo {
    String accessToken;
    String refreshToken;
}
