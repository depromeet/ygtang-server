package inspiration.auth;

import inspiration.ResultResponse;
import inspiration.auth.jwt.JwtProvider;
import inspiration.auth.request.LoginRequest;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.RefreshTokenException;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.response.MemberInfoResponse;
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

        String accessToken = redisService.getData(accessTokenKey + member.getId());
        String refreshToken = redisService.getData(refreshTokenKey + member.getId());


        if (accessToken == null && refreshToken == null) {
            TokenResponse tokenResponse = jwtProvider.createTokenDto(member.getId());

            saveAccessToken(member.getId(), tokenResponse.getAccessToken());
            saveRefreshToken(member.getId(), tokenResponse.getRefreshToken());

            return ResultResponse.of(issueToken, tokenResponse);
        }

        if (accessToken == null) {
            accessToken = jwtProvider.createAccessToken(member.getId());

            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .memberId(member.getId())
                    .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                    .build();

            saveAccessToken(member.getId(), tokenResponse.getAccessToken());

            return ResultResponse.of(issueToken, tokenResponse);
        }

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                .build();

        return ResultResponse.of(issueToken, tokenResponse);
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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

        return MemberInfoResponse.of(member);
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {

        redisTemplate.opsForValue().set(refreshTokenKey + memberId, refreshToken, ExpireTimeConstants.refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

    private void saveAccessToken(Long memberId, String refreshToken) {

        redisTemplate.opsForValue().set(accessTokenKey + memberId, refreshToken, ExpireTimeConstants.refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

    private Member checkEmail(String email) {

        return memberRepository.findByEmail(email).orElseThrow(() -> new PostNotFoundException(ExceptionType.EMAIl_NOT_FOUND.getMessage()));
    }

    private void verifyPassword(String requestPassword, String realPassword) {

        if (!passwordEncoder.matches(requestPassword, realPassword)) {
            throw new PostNotFoundException(ExceptionType.PASSWORD_NOT_MATCHED.getMessage());
        }
    }
}
