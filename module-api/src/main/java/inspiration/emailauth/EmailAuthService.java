package inspiration.emailauth;

import inspiration.emailauth.request.ConfirmEmailRequest;
import inspiration.emailauth.request.EmailAuthRequest;
import inspiration.enumeration.ExceptionType;
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
    public void confirmEmail(ConfirmEmailRequest request) {

        isEmailAuth(request.getEmail());

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH.getKey() + request.getEmail(), authToken, 60 * 5L);

        emailSendService.send(request.getEmail(), authToken);
    }

    @Transactional
    public RedirectView emailAuth(EmailAuthRequest request) {

        String expiredKey = RedisKey.EAUTH.getKey() + request.getEmail();

        if (redisService.getData(expiredKey) == null)
            throw new EmailAuthenticatedTimeExpiredException();

        redisService.deleteData(expiredKey);

        emailAuthRepository.save(
                EmailAuth.builder()
                        .email(request.getEmail())
                        .isAuth(true)
                        .build());

        return PolicyRedirectViewUtil.redirectView();
    }

    private void isEmailAuth(String email) {
        if (emailAuthRepository.findByEmail(email).isPresent()) {
            throw new PostNotFoundException(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage());
        }
    }
}
