package inspiration.emailauth;

import inspiration.emailauth.request.authenticateEmailRequest;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.RedisKey;
import inspiration.exception.ConflictRequestException;
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
    private final EmailSendService emailSendService;
    private final RedisService redisService;

    @Transactional
    public void sendEmail(String email) {

        isEmailAuth(email);

        String authToken = AuthTokenUtil.getAuthToken();

        redisService.setDataWithExpiration(RedisKey.EAUTH.getKey() + email, authToken, 60 * 5L);

        emailSendService.send(email, authToken);
    }

    @Transactional
    public RedirectView authenticateEmail(authenticateEmailRequest request) {

        String expiredKey = RedisKey.EAUTH.getKey() + request.getEmail();

        if (redisService.getData(expiredKey) == null) {
            throw new EmailAuthenticatedTimeExpiredException();
        }

        redisService.deleteData(expiredKey);

        emailAuthRepository.save(
                EmailAuth.builder()
                        .email(request.getEmail())
                        .isAuth(true)
                        .build());

        return PolicyRedirectViewUtil.redirectView();
    }

    private void isEmailAuth(String email) {

        if (emailAuthRepository.existsByEmail(email)) {
            throw new PostNotFoundException(ExceptionType.EMAIL_ALREADY_AUTHENTICATED.getMessage());
        }
    }
}
