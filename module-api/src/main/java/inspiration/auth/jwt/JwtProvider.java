package inspiration.auth.jwt;

import inspiration.auth.TokenResponseVo;
import inspiration.enumeration.ExpireTimeConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final String MEMBER_ID = "member_id";
    @Value("spring.jwt.secret")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64UrlCodec.BASE64URL.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long memberId) {
        return createAccessTokenInner(memberId);
    }

    private String createAccessTokenInner(Long memberId) {
        Claims accessTokenClaims = Jwts.claims().setSubject(String.valueOf(memberId));
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

    public String createRefreshToken(Long memberId) {
        return createRefreshTokenInner(memberId);
    }

    private String createRefreshTokenInner(Long memberId) {
        Claims refreshTokenClaims = Jwts.claims().setSubject(String.valueOf(memberId));
        refreshTokenClaims.put(MEMBER_ID, memberId);

        Date now = new Date();

        return Jwts.builder()
                   .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                   .setClaims(refreshTokenClaims)
                   .setExpiration(new Date(now.getTime() + ExpireTimeConstants.refreshTokenValidMillisecond))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    public TokenResponseVo createTokenVo(Long memberId) {
        return TokenResponseVo.builder()
                              .accessToken(createAccessTokenInner(memberId))
                              .refreshToken(createRefreshTokenInner(memberId))
                              .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                              .memberId(memberId)
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
        try {
            return Optional.ofNullable(claims.getSubject())
                           .map(Long::parseLong);
        } catch (NumberFormatException e) {
            log.error("잘못된 토큰입니다. 'sub' claim 이 Long 타입이어야합니다. token: {}", token);
            return Optional.empty();
        }
    }
}
