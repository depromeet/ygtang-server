package inspiration.signup;

import inspiration.ResultResponse;
import inspiration.emailauth.EmailAuthRepository;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.PostNotFoundException;
import inspiration.member.MemberRepository;
import inspiration.member.request.SignUpRequest;
import inspiration.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(SignUpRequest request) {

        verifyEmail(request.getEmail());
        isValidEmail(request.getEmail());
        isValidNickName(request.getNickName());
        confirmPasswordCheck(request.getConfirmPassword(), request.getPassword());

        return memberRepository.save(request.toEntity(passwordEncoder)).getId();
    }

    @Transactional(readOnly = true)
    public ResultResponse checkNickName(String nickname) {

        isValidNickName(nickname);

        return ResultResponse.from("사용할 수 있는 닉네임입니다.");
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
}
