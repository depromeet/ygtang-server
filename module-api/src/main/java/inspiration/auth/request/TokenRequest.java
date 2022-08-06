package inspiration.auth.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {
    String accessToken;
    String refreshToken;
}
