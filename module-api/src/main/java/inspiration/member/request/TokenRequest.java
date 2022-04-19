package inspiration.member.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    private String accessToken;
    private String refreshToken;
}
