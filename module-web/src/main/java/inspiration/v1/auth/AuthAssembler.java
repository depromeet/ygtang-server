package inspiration.v1.auth;

import inspiration.auth.TokenResponseVo;
import inspiration.auth.request.LoginRequestVo;
import inspiration.auth.request.TokenRequestVo;
import org.springframework.stereotype.Component;

@Component
public class AuthAssembler {
    public TokenRequestVo toTokenRequestVo(TokenRequest tokenRequest) {
        return new TokenRequestVo(
                tokenRequest.getAccessToken(),
                tokenRequest.getRefreshToken()
        );
    }

    public LoginRequestVo toLoginRequestVo(LoginRequest loginRequest) {
        return new LoginRequestVo(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
    }

    public TokenResponse toTokenResponse(TokenResponseVo tokenResponseVo) {
        return new TokenResponse(
                tokenResponseVo.getAccessToken(),
                tokenResponseVo.getRefreshToken(),
                tokenResponseVo.getAccessTokenExpireDate(),
                tokenResponseVo.getMemberId()
        );
    }
}
