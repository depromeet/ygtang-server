package inspiration.auth;

import lombok.*;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("ClassCanBeRecord")
public class TokenResponseVo {
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpireDate;
    private final long memberId;
}
