package inspiration.domain.emailauth;

import inspiration.ResultResponse;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.RedisKey;
import inspiration.exception.EmailAuthenticatedTimeExpiredException;
import inspiration.exception.PostNotFoundException;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.passwordauth.PasswordAuth;
import inspiration.domain.passwordauth.PasswordAuthRepository;
import inspiration.redis.RedisService;
import inspiration.utils.AuthTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        if(email.contains("+")) {
            throw new PostNotFoundException("잘못된 이메일 형식입니다.");
        }

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH_SIGN_UP.getKey() + email, authToken, ExpireTimeConstants.expireSingUpAccessTokenTime);

        signUpEmailSendService.send(email, authToken);
    }

    @Transactional
    public void authenticateEmailOfSignUp(String email) {

        String expiredKey = RedisKey.EAUTH_SIGN_UP.getKey() + email;
        log.info(email);

        if (redisService.getData(expiredKey) == null) {
            throw new EmailAuthenticatedTimeExpiredException();
        }

        redisService.deleteData(expiredKey);

        emailAuthRepository.save(
                EmailAuth.builder()
                        .email(email)
                        .isAuth(true)
                        .build());
    }

    @Transactional
    public void resetPasswordForAuthEmailSend(String email) {

        if (!memberRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.MEMBER_NOT_FOUND.getMessage());
        }

        if(passwordAuthRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage());
        }

        if(email.contains("+")) {
            throw new PostNotFoundException("잘못된 이메일 형식입니다.");
        }

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH_RESET_PASSWORD.getKey() + email, authToken, ExpireTimeConstants.expireSingUpAccessTokenTime);

        resetPasswordForAuthSendService.send(email, authToken);
    }

    @Transactional
    public void authenticateEmailOfResetPasswordForAuth(String email) {

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
