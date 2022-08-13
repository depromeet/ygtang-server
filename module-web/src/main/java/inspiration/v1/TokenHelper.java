package inspiration.v1;

import inspiration.ResultResponse;
import inspiration.auth.TokenResponse;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenHelper {
    @Autowired
    private HttpServletResponse response;

    public void addHeaderForAccessToken(ResultResponse<TokenResponse> loginResponse) {
        String accessToken = loginResponse.getData().getAccessToken();
        response.setHeader(TokenType.ACCESS_TOKEN.getMessage(), accessToken);
    }

    public void addCookieForRefreshToken(ResultResponse<TokenResponse> loginResponse) {
        String refreshToken = loginResponse.getData().getRefreshToken();
        Cookie cookie = new Cookie(TokenType.REFRESH_TOKEN.getMessage(), refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(Math.toIntExact(ExpireTimeConstants.accessTokenValidMillisecond));
        response.addCookie(cookie);
    }
}
