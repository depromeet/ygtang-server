package inspiration.v1.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {
    String accessToken;
    String refreshToken;
}
