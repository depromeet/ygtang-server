package inspiration.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    private String accessToken;
    private String refreshToken;
}
