package inspiration.signup;

import inspiration.auth.TokenResponseVo;
import inspiration.auth.jwt.JwtProvider;
import inspiration.domain.emailauth.EmailAuthRepository;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.request.ExtraInfoRequestVo;
import inspiration.domain.member.request.SignUpRequestVo;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SignupService {
    private static final String REFRESH_TOKEN_KEY = "refreshToken : ";

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;


    @Transactional
    public TokenResponseVo signUp(SignUpRequestVo signUpRequestVo) {
        verifyEmail(signUpRequestVo.getEmail());
        isValidEmail(signUpRequestVo.getEmail());
        isValidNickName(signUpRequestVo.getNickName());
        confirmPasswordCheck(signUpRequestVo.getConfirmPassword(), signUpRequestVo.getPassword());

        Member member = memberRepository.save(signUpRequestVo.toEntity(passwordEncoder));
        TokenResponseVo tokenResponseVo = jwtProvider.createTokenVo(member.getId());
        saveRefreshToken(member.getId(), tokenResponseVo.getRefreshToken());
        return tokenResponseVo;
    }

    @Transactional(readOnly = true)
    public void checkNickName(String nickname) {
        isValidNickName(nickname);
    }

    @Transactional
    public void updateExtraInfo(String email, ExtraInfoRequestVo requestVo) {
        Member member = memberRepository.findByEmail(email)
                                        .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));
        member.updateExtraInfo(requestVo.getGender(), requestVo.getAge(), requestVo.getJob());
    }

    public boolean validSignUpEmailStatus(String email) {
        return memberRepository.existsByEmail(email);
    }

    private void confirmPasswordCheck(String confirmPasswordCheck, String password) {

        if (!confirmPasswordCheck.equals(password)) {
            throw new PostNotFoundException(ExceptionType.PASSWORD_NOT_MATCHED.getMessage());
        }
    }

    private void verifyEmail(String email) {

        if (!emailAuthRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage());
        }
    }

    private void isValidNickName(String nickName) {

        if (memberRepository.existsByNickname(nickName)) {
            throw new ConflictRequestException(ExceptionType.EXISTS_NICKNAME.getMessage());
        }
    }

    private void isValidEmail(String email) {

        if (memberRepository.existsByEmail(email)) {
            throw new ConflictRequestException(ExceptionType.EXISTS_EMAIL.getMessage());
        }
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY + memberId, refreshToken, ExpireTimeConstants.refreshTokenValidMillisecond, TimeUnit.MILLISECONDS);
    }

}
