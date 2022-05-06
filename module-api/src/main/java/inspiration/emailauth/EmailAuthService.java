package inspiration.emailauth;

import inspiration.ResultResponse;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.RedisKey;
import inspiration.exception.EmailAuthenticatedTimeExpiredException;
import inspiration.exception.PostNotFoundException;
import inspiration.member.MemberRepository;
import inspiration.member.MemberService;
import inspiration.redis.RedisService;
import inspiration.utils.AuthTokenUtil;
import inspiration.utils.PolicyRedirectViewUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Service
public class EmailAuthService {

    private final EmailAuthRepository emailAuthRepository;
    private final SignUpEmailSendService signUpEmailSendService;
    private final UpdatePasswordEmailSendService updatePasswordEmailSendService;
    private final RedisService redisService;

    @Transactional
    public void signUpEmailSend(String email) {

        verifyEmail(email);

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH_SIGN_UP.getKey() + email, authToken, ExpireTimeConstants.expireSingUpAccessTokenTime);

        signUpEmailSendService.send(email, authToken);
    }

    @Transactional
    public void updatePasswordEmailSend(String email) {

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH_UPDATE_PASSWORD.getKey() + email, authToken, ExpireTimeConstants.expireUpdatePasswordAccessTokenTime);

        updatePasswordEmailSendService.send(email, authToken);
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
    public RedirectView authenticateEmailOfUpdatePassword(String email) {

        String expiredKey = RedisKey.EAUTH_UPDATE_PASSWORD.getKey() + email;
        System.out.println(expiredKey);
        System.out.println(redisService.getData(expiredKey));
        if (redisService.getData(expiredKey) == null) {
            throw new EmailAuthenticatedTimeExpiredException();
        }

        return PolicyRedirectViewUtil.redirectView();
    }

    @Transactional(readOnly = true)
    public ResultResponse validAuthenticateEmailStatus(String email) {

        if (emailAuthRepository.existsByEmail(email)) {

            return ResultResponse.of(ExceptionType.USER_EXISTS.getMessage(), true);
        }

        return ResultResponse.of(ExceptionType.USER_NOT_EXISTS.getMessage(), false);
    }

    private void verifyEmail(String email) {

        if (emailAuthRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage());
        }
    }
}
