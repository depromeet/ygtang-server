package inspiration.member.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
public class TokenRequest {

    public TokenRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    private String accessToken;
    private String refreshToken;
}
