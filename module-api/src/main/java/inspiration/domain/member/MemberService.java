package inspiration.domain.member;

import inspiration.domain.emailauth.EmailAuthRepository;
import inspiration.domain.emailauth.ResetPasswordEmailSendService;
import inspiration.domain.member.response.MemberResponseVo;
import inspiration.domain.passwordauth.PasswordAuth;
import inspiration.domain.passwordauth.PasswordAuthRepository;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.UnauthorizedAccessRequestException;
import inspiration.utils.GetResetPasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
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
                                        .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

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

    public List<MemberResponseVo> findAll() {
        return memberRepository.findAll()
                               .stream()
                               .map(MemberResponseVo::of)
                               .collect(Collectors.toList());
    }

}
