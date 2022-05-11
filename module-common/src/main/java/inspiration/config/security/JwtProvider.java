package inspiration.config.security;

import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.TokenType;
import inspiration.exception.UnauthorizedAccessRequestException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.lang.String;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("spring.jwt.secret")
    private String secretKey;
    private String ROLES = "roles";
    private String MEMBER_ID = "member_id";
    private final UserDetailsService userDetailsService;
    private final HttpServletResponse httpServletResponse;

    @PostConstruct
    protected void init() {
        secretKey = Base64UrlCodec.BASE64URL.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse createTokenDto(Long memberId, List<String> roles) {

        Claims accessTokenClaims = Jwts.claims().setSubject(String.valueOf(memberId));
        accessTokenClaims.put(ROLES, roles);
        accessTokenClaims.put(MEMBER_ID, memberId);

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(accessTokenClaims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ExpireTimeConstants.accessTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        httpServletResponse.setHeader(TokenType.ACCESS_TOKEN.getMessage(), accessToken);

        Claims refreshTokenClaims = Jwts.claims().setSubject(String.valueOf(memberId));
        refreshTokenClaims.put(ROLES, roles);
        refreshTokenClaims.put(MEMBER_ID, memberId);

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(refreshTokenClaims)
                .setExpiration(new Date(now.getTime() + ExpireTimeConstants.refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Cookie cookie = new Cookie(TokenType.REFRESH_TOKEN.getMessage(), refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(Math.toIntExact(ExpireTimeConstants.accessTokenValidMillisecond));
        httpServletResponse.addCookie(cookie);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                .build();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        if (claims.get(ROLES) == null) {
            throw new UnauthorizedAccessRequestException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(TokenType.ACCESS_TOKEN.getMessage());
    }

    public boolean validationToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }
        return false;
    }
}
