package inspiration.auth;

import inspiration.ResultResponse;
import inspiration.auth.request.LoginRequest;
import inspiration.config.security.JwtProvider;
import inspiration.config.security.TokenResponse;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.RefreshTokenException;
import inspiration.member.Member;
import inspiration.member.MemberRepository;
import inspiration.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
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
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final String refreshTokenKey = "refreshToken : ";
    private final String issueToken = "refreshToken : ";

    @Transactional
    public ResultResponse login(LoginRequest request) {

        Member member = checkEmail(request.getEmail());

        verifyPassword(request.getPassword(), member.getPassword());

        TokenResponse tokenResponse = jwtProvider.createTokenDto(member.getId(), member.getRoles());

        saveRefreshToken(member.getId(), tokenResponse.getRefreshToken());

        return ResultResponse.of(issueToken, tokenResponse);
    }

    @Transactional
    public ResultResponse reissue(String accessTokenRequest, String refreshTokenRequest) {

        if (!jwtProvider.validationToken(refreshTokenRequest)) {
            throw new RefreshTokenException();
        }

        Authentication authentication = jwtProvider.getAuthentication(accessTokenRequest);

        Member member = memberRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.MEMBER_NOT_FOUND.getMessage()));

        String refreshToken = redisTemplate.opsForValue().get(refreshTokenKey + member.getId());

        if (refreshToken == null) {
            throw new RefreshTokenException();
        }

        if (!refreshToken.equals(refreshTokenRequest)) {
            throw new RefreshTokenException(ExceptionType.VALID_NOT_REFRESH_TOKEN.getMessage());
        }

        TokenResponse newTokenResponse = jwtProvider.createTokenDto(member.getId(), member.getRoles());

        saveRefreshToken(member.getId(), newTokenResponse.getRefreshToken());

        return ResultResponse.of(issueToken, newTokenResponse);
    }

    @Transactional(readOnly = true)
    public String getUserInfo() {

        memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

        String userEmail = memberRepository.findById(SecurityUtil.getCurrentMemberId()).get().getEmail();

        log.info("현재 로그인한 사용자: " + userEmail);

        return userEmail;
    }
    private void saveRefreshToken(Long memberId, String refreshToken) {

        redisTemplate.opsForValue().set(refreshTokenKey + memberId, refreshToken, ExpireTimeConstants.refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
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
