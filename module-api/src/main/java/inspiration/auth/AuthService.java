package inspiration.auth;

import inspiration.auth.jwt.JwtProvider;
import inspiration.auth.request.LoginRequestVo;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.response.MemberInfoVo;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.RefreshTokenException;
import inspiration.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final String refreshTokenKey = "refreshToken : ";
    private final String accessTokenKey = "accessToken : ";

    @Transactional
    public TokenResponseVo login(LoginRequestVo loginRequestVo) {
        Member member = checkEmail(loginRequestVo.getEmail());
        verifyPassword(loginRequestVo.getPassword(), member.getPassword());

        return new TokenResponseVo(
                resolveAccessToken(member.getId()),
                resolveRefreshToken(member.getId()),
                member.getId(),
                ExpireTimeConstants.accessTokenValidMillisecond
        );
    }

    private Member checkEmail(String email) {
        return memberRepository.findByEmail(email)
                               .orElseThrow(() -> new PostNotFoundException(ExceptionType.EMAIl_NOT_FOUND.getMessage()));
    }

    private void verifyPassword(String requestPassword, String realPassword) {
        if (!passwordEncoder.matches(requestPassword, realPassword)) {
            throw new PostNotFoundException(ExceptionType.PASSWORD_NOT_MATCHED.getMessage());
        }
    }

    private String resolveAccessToken(Long memberId) {
        String accessToken = redisService.getData(accessTokenKey + memberId);
        boolean isValid = accessToken != null && jwtProvider.resolveMemberId(accessToken).isPresent();
        if (isValid) {
            return accessToken;
        }
        String createdAccessToken = jwtProvider.createAccessToken(memberId);
        saveAccessToken(memberId, createdAccessToken);
        return createdAccessToken;
    }

    private void saveAccessToken(Long memberId, String accessToken) {
        redisTemplate.opsForValue().set(accessTokenKey + memberId, accessToken, ExpireTimeConstants.refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

    private String resolveRefreshToken(Long memberId) {
        String refreshToken = redisService.getData(refreshTokenKey + memberId);
        boolean isValid = refreshToken != null && jwtProvider.resolveMemberId(refreshToken).isPresent();
        if (isValid) {
            return refreshToken;
        }
        String createdRefreshToken = jwtProvider.createRefreshToken(memberId);
        saveRefreshToken(memberId, createdRefreshToken);
        return createdRefreshToken;
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(refreshTokenKey + memberId, refreshToken, ExpireTimeConstants.refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

    @Transactional
    public TokenResponseVo reissue(String refreshToken) {
        Long memberId = jwtProvider.resolveMemberId(refreshToken)
                                   .orElseThrow(RefreshTokenException::new);
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new PostNotFoundException(ExceptionType.MEMBER_NOT_FOUND.getMessage()));

        String refreshTokenInRedis = redisTemplate.opsForValue().get(refreshTokenKey + member.getId());

        if (refreshTokenInRedis == null) {
            throw new RefreshTokenException();
        }

        if (!refreshTokenInRedis.equals(refreshToken)) {
            throw new RefreshTokenException(ExceptionType.VALID_NOT_REFRESH_TOKEN.getMessage());
        }

        TokenResponseVo newTokenResponseVo = jwtProvider.createTokenDto(member.getId());
        saveRefreshToken(member.getId(), newTokenResponseVo.getRefreshToken());
        saveAccessToken(member.getId(), newTokenResponseVo.getAccessToken());
        return newTokenResponseVo;
    }

    @Transactional(readOnly = true)
    public MemberInfoVo getUserInfo(Long memberId) {
        return memberRepository.findById(memberId)
                               .map(MemberInfoVo::from)
                               .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));
    }
}
