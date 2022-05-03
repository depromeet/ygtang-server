package inspiration.auth;

import inspiration.auth.request.LoginRequest;
import inspiration.config.security.JwtProvider;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.RefreshTokenException;
import inspiration.member.Member;
import inspiration.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final Long refreshTokenValidMillisecond = 14 * 24 * 60 * 60 * 1000L;
    private final String refreshTokenKey = "refreshToken : ";

    @Transactional
    public void login(LoginRequest request) {

        Member member = checkEmail(request.getEmail());

        isValidPassword(request.getPassword(), member.getPassword());

        refreshTokenSave(member.getId(), jwtProvider.createTokenDto(member.getId(), member.getRoles()));
    }

    @Transactional
    public void reissue(String accessToken) {
        System.out.println(accessToken);

        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        Member member = memberRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.MEMBER_NOT_FOUND.getMessage()));

        String refreshToken = redisTemplate.opsForValue().get(refreshTokenKey + member.getId());

        if (refreshToken == null) {
            throw new RefreshTokenException();
        }

        String newCreatedToken = jwtProvider.createTokenDto(member.getId(), member.getRoles());

        refreshTokenSave(member.getId(), newCreatedToken);
    }

    private void refreshTokenSave(Long memberId, String refreshToken) {

        redisTemplate.opsForValue().set(refreshTokenKey + memberId, refreshToken, refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

    private Member checkEmail(String email) {

        return memberRepository.findByEmail(email).orElseThrow(() -> new PostNotFoundException(ExceptionType.EMAIl_NOT_FOUND.getMessage()));
    }

    private void isValidPassword(String requestPassword, String realPassword) {

        if (!passwordEncoder.matches(requestPassword, realPassword))
            throw new PostNotFoundException(ExceptionType.VALID_NOT_PASSWORD.getMessage());
    }
}
