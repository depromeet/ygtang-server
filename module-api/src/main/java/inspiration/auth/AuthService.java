package inspiration.auth;

import inspiration.ResultResponse;
import inspiration.auth.jwt.JwtProvider;
import inspiration.auth.request.LoginRequest;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.response.MemberInfoResponse;
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
    private final String issueToken = "refreshToken : ";

    @Transactional
    public ResultResponse<TokenResponse> login(LoginRequest request) {

        Member member = checkEmail(request.getEmail());
        verifyPassword(request.getPassword(), member.getPassword());

        TokenResponse tokenResponse = TokenResponse.builder()
                                                   .accessToken(resolveAccessToken(member.getId()))
                                                   .refreshToken(resolveRefreshToken(member.getId()))
                                                   .memberId(member.getId())
                                                   .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                                                   .build();

        return ResultResponse.of(issueToken, tokenResponse);
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
    public ResultResponse reissue(String refreshToken) {
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

        TokenResponse newTokenResponse = jwtProvider.createTokenDto(member.getId());

        saveRefreshToken(member.getId(), newTokenResponse.getRefreshToken());
        saveAccessToken(member.getId(), newTokenResponse.getAccessToken());

        return ResultResponse.of(issueToken, newTokenResponse);
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getUserInfo(Long memberId) {
        return memberRepository.findById(memberId)
                               .map(MemberInfoResponse::of)
                               .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));
    }
}
