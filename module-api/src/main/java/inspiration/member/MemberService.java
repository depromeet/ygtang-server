package inspiration.member;

import inspiration.emailauth.EmailAuthRepository;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.PostNotFoundException;
import inspiration.member.request.SignUpRequest;
import inspiration.member.response.NicknameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthRepository emailAuthRepository;

    @Transactional(readOnly = true)
    public NicknameResponse confirmNickName(String nickname) {
        existsByNickName(nickname);

        return NicknameResponse.of();
    }

    @Transactional
    public Long signUp(SignUpRequest request) {

        isEmailAuth(request.getEmail());
        duplicationEmailCheck(request);

        return memberRepository.save(request.toEntity(passwordEncoder)).getId();
    }

    private void duplicationEmailCheck(SignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictRequestException(ExceptionType.EXISTS_EMAIL.getMessage());
        }
    }

    private void existsByNickName(String nickName) {
        if (memberRepository.existsByNickname(nickName)) {
            throw new ConflictRequestException(ExceptionType.EXISTS_NICKNAME.getMessage());
        }
    }

    private void isEmailAuth(String email) {
        if (emailAuthRepository.findByEmail(email).isEmpty()) {
            throw new PostNotFoundException(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage());
        }
    }
}
