package inspiration.emailauth;

import inspiration.ResultResponse;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.enumeration.RedisKey;
import inspiration.exception.EmailAuthenticatedTimeExpiredException;
import inspiration.exception.PostNotFoundException;
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
    private final EmailSendService emailSendService;
    private final RedisService redisService;

    @Transactional
    public void sendEmail(String email) {

        verifyEmail(email);

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH.getKey() + email, authToken, ExpireTimeConstants.expireAccessTokenTime);

        emailSendService.send(email, authToken);
    }

    @Transactional
    public RedirectView authenticateEmail(String email) {

        String expiredKey = RedisKey.EAUTH.getKey() + email;

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

    public ResultResponse validAuthenticateEmailStatus(String email) {

        if (emailAuthRepository.existsByEmail(email)) {

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
