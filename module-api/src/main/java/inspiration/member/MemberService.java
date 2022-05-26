package inspiration.member;

import inspiration.emailauth.EmailAuthRepository;
import inspiration.emailauth.ResetPasswordEmailSendService;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.UnauthorizedAccessRequestException;
import inspiration.passwordauth.PasswordAuth;
import inspiration.passwordauth.PasswordAuthRepository;
import inspiration.utils.GetResetPasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmailAuthRepository emailAuthRepository;
    private final MemberRepository memberRepository;
    private final PasswordAuthRepository passwordAuthRepository;
    private final ResetPasswordEmailSendService resetPasswordEmailSendService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(Long memberId, String confirmPassword, String password) {

        confirmPasswordCheck(confirmPassword, password);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

        member.updatePassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void resetPasswordEmailSend(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

        String resetPassword = GetResetPasswordUtil.getResetPassword();

        resetPasswordEmailSendService.send(email, resetPassword);

        member.updatePassword(passwordEncoder.encode(resetPassword));

        PasswordAuth passwordAuth = passwordAuthRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage()));

        passwordAuthRepository.delete(passwordAuth);
    }

    @Transactional
    public void changeNickname(Long memberId, String nickname) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_EXISTS.getMessage()));

        member.updateNickname(nickname);
    }

    @Transactional
    public void removeUser(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_EXISTS.getMessage()));

        emailAuthRepository.deleteByEmail(member.getEmail());

        memberRepository.deleteById(memberId);

    }

    private void confirmPasswordCheck(String confirmPasswordCheck, String password) {

        if (!confirmPasswordCheck.equals(password)) {
            throw new PostNotFoundException(ExceptionType.PASSWORD_NOT_MATCHED.getMessage());
        }
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(UnauthorizedAccessRequestException::new);
    }
}
