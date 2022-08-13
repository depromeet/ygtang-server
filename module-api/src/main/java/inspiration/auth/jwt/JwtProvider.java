package inspiration.auth.jwt;

import inspiration.auth.TokenResponse;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("spring.jwt.secret")
    private String secretKey;
    private final String ROLES = "roles";
    private final String MEMBER_ID = "member_id";

    @PostConstruct
    protected void init() {
        secretKey = Base64UrlCodec.BASE64URL.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long memberId, List<String> roles) {

        Claims accessTokenClaims = Jwts.claims().setSubject(String.valueOf(memberId));
        accessTokenClaims.put(ROLES, roles);
        accessTokenClaims.put(MEMBER_ID, memberId);

        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(accessTokenClaims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ExpireTimeConstants.accessTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
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

        Claims refreshTokenClaims = Jwts.claims().setSubject(String.valueOf(memberId));
        refreshTokenClaims.put(ROLES, roles);
        refreshTokenClaims.put(MEMBER_ID, memberId);

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(refreshTokenClaims)
                .setExpiration(new Date(now.getTime() + ExpireTimeConstants.refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(memberId)
                .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                .build();
    }

    public Optional<Long> resolveMemberId(String token) {
        final Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다. token: {}", token, e);
            return Optional.empty();
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다. token: {}", token, e);
            return Optional.empty();
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다. token: {}", token, e);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다. token: {}", token, e);
            return Optional.empty();
        }
        if (claims.get(ROLES) == null) {
            log.error("잘못된 토큰입니다. 'roles' claim 이 있어야합니다. token: {}", token);
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(claims.getSubject())
                           .map(Long::parseLong);
        } catch (NumberFormatException e) {
            log.error("잘못된 토큰입니다. 'sub' claim 이 Long 타입이어야합니다. token: {}", token);
            return Optional.empty();
        }
    }
}
