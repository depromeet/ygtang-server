package inspiration.member;

import inspiration.ResultResponse;
import inspiration.emailauth.EmailAuthRepository;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.UnauthorizedAccessRequestException;
import inspiration.member.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ResultResponse checkNickName(String nickname) {

        isValidNickName(nickname);

        return ResultResponse.from("사용할 수 있는 닉네임입니다.");
    }

    @Transactional
    public Long signUp(SignUpRequest request) {

        verifyEmail(request.getEmail());
        isValidEmail(request.getEmail());
        isValidNickName(request.getNickName());
        confirmPasswordCheck(request.getConfirmPassword(), request.getPassword());

        return memberRepository.save(request.toEntity(passwordEncoder)).getId();
    }

    private void isValidEmail(String email) {

        if (memberRepository.existsByEmail(email)) {
            throw new ConflictRequestException(ExceptionType.EXISTS_EMAIL.getMessage());
        }
    }

    private void confirmPasswordCheck(String confirmPasswordCheck, String password) {

        if (!confirmPasswordCheck.equals(password)) {
            throw new PostNotFoundException(ExceptionType.VALID_NOT_PASSWORD.getMessage());
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

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(UnauthorizedAccessRequestException::new);
    }
}
