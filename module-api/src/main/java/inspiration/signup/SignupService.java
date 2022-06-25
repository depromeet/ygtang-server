package inspiration.signup;

import inspiration.ResultResponse;
import inspiration.auth.JwtProvider;
import inspiration.auth.TokenResponse;
import inspiration.emailauth.EmailAuthRepository;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.PostNotFoundException;
import inspiration.member.Member;
import inspiration.member.MemberRepository;
import inspiration.member.request.SignUpRequest;
import inspiration.member.request.ExtraInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final String REFRESH_TOKEN_KEY = "refreshToken : ";

    @Transactional
    public ResultResponse signUp(SignUpRequest request) {

        verifyEmail(request.getEmail());
        isValidEmail(request.getEmail());
        isValidNickName(request.getNickName());
        confirmPasswordCheck(request.getConfirmPassword(), request.getPassword());

        Member member = memberRepository.save(request.toEntity(passwordEncoder));
        TokenResponse tokenResponse = jwtProvider.createTokenDto(member.getId(), member.getRoles());
        saveRefreshToken(member.getId(), tokenResponse.getRefreshToken());

        return ResultResponse.of(REFRESH_TOKEN_KEY, tokenResponse);
    }

    @Transactional(readOnly = true)
    public ResultResponse checkNickName(String nickname) {

        isValidNickName(nickname);

        return ResultResponse.from("사용할 수 있는 닉네임입니다.");
    }

    @Transactional
    public void updateExtraInfo(String email, ExtraInfoRequest request) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

        member.updateExtraInfo(request.getGender(), request.getAge(), request.getJob());
    }

    public ResultResponse validSignUpEmailStatus(String email) {

        if (memberRepository.existsByEmail(email)) {

            return ResultResponse.of(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage(), true);
        }

        return ResultResponse.of(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage(), false);
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
