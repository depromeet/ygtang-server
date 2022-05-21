package inspiration.emailauth;

import inspiration.ResultResponse;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.RedisKey;
import inspiration.exception.EmailAuthenticatedTimeExpiredException;
import inspiration.exception.PostNotFoundException;
import inspiration.member.Member;
import inspiration.member.MemberRepository;
import inspiration.passwordauth.PasswordAuth;
import inspiration.passwordauth.PasswordAuthRepository;
import inspiration.redis.RedisService;
import inspiration.utils.AuthTokenUtil;
import inspiration.utils.GetResetPasswordUtil;
import inspiration.utils.PolicyRedirectViewUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Service
public class EmailAuthService {

    private final EmailAuthRepository emailAuthRepository;
    private final MemberRepository memberRepository;
    private final PasswordAuthRepository passwordAuthRepository;
    private final SignUpEmailSendService signUpEmailSendService;
    private final ResetPasswordForAuthSendService resetPasswordForAuthSendService;
    private final RedisService redisService;

    @Transactional
    public void signUpEmailSend(String email) {

        verifyEmail(email);

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH_SIGN_UP.getKey() + email, authToken, ExpireTimeConstants.expireSingUpAccessTokenTime);

        signUpEmailSendService.send(email, authToken);
    }

    @Transactional
    public RedirectView authenticateEmailOfSingUp(String email) {

        String expiredKey = RedisKey.EAUTH_SIGN_UP.getKey() + email;

        if (redisService.getData(expiredKey) == null) {
            throw new EmailAuthenticatedTimeExpiredException();
        }

        redisService.deleteData(expiredKey);

        emailAuthRepository.save(
                EmailAuth.builder()
                        .email(email)
                        .isAuth(true)
                        .build());

        return PolicyRedirectViewUtil.redirectView();
    }

    @Transactional
    public void resetPasswordForAuthEmailSend(String email) {

        if (!memberRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.MEMBER_NOT_FOUND.getMessage());
        }

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH_RESET_PASSWORD.getKey() + email, authToken, ExpireTimeConstants.expireSingUpAccessTokenTime);

        resetPasswordForAuthSendService.send(email, authToken);
    }

    @Transactional
    public RedirectView authenticateEmailOfResetPasswordForAuth(String email) {

        String expiredKey = RedisKey.EAUTH_RESET_PASSWORD.getKey() + email;

        if (redisService.getData(expiredKey) == null) {
            throw new EmailAuthenticatedTimeExpiredException();
        }

        redisService.deleteData(expiredKey);

        passwordAuthRepository.save(
                PasswordAuth.builder()
                        .email(email)
                        .isAuth(true)
                        .build());

        return PolicyRedirectViewUtil.redirectView();
    }

    @Transactional(readOnly = true)
    public ResultResponse validAuthenticateEmailStatusOfSignup(String email) {

        if (emailAuthRepository.existsByEmail(email)) {
            return ResultResponse.of(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage(), true);
        }

        return ResultResponse.of(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage(), false);
    }

    @Transactional(readOnly = true)
    public ResultResponse validAuthenticateEmailStatusOfResetPassword(String email) {

        if (passwordAuthRepository.existsByEmail(email)) {
            return ResultResponse.of(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage(), true);
        }

        return ResultResponse.of(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage(), false);
    }

    private void verifyEmail(String email) {

        if (emailAuthRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage());
        }
    }
}
