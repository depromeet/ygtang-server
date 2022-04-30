package inspiration.auth;

import inspiration.auth.request.LoginRequest;
import inspiration.config.security.JwtProvider;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.member.Member;
import inspiration.member.MemberRepository;
import inspiration.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final Long refreshTokenValidMillisecond = 14 * 24 * 60 * 60 * 1000L;
    private Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public void login(LoginRequest request, HttpServletResponse httpServletResponse) {

        Member member = existsEmailCheck(request.getEmail());

        isValidPassword(request.getPassword(), member.getPassword());

        refreshTokenSave(member.getId(), jwtProvider.createTokenDto(member.getId(), member.getRoles(), httpServletResponse));
    }

    @Transactional
    public void getUserInfo() {
        loginUserCheck();
    }

    private void refreshTokenSave(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set("refreshToken : " + memberId, refreshToken, refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

    private Member existsEmailCheck(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.EMAIl_NOT_FOUND.getMessage()));
    }

    private void isValidPassword(String requestPassword, String realPassword) {
        if (!passwordEncoder.matches(requestPassword, realPassword))
            throw new PostNotFoundException(ExceptionType.VALID_NOT_PASSWORD.getMessage());
    }

    private Member loginUserCheck() {
        Optional<Member> member = memberRepository.findById(SecurityUtil.getCurrentMemberId());
        logger.info("현재 로그인한 사용자: " + member.get().getEmail());
        return member.orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));
    }
}
