package inspiration.domain.emailauth;

import inspiration.email.AwsSesService;
import inspiration.email.GoogleService;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordEmailSendService implements EmailSendService {

    private final static String SUBJECT = "비빌번호 초기화";
    private final static String CREATED_RESET_PASSWORD = "임시 비밀번호 발급: ";
    private final AwsSesService awsSesService;
    private final GoogleService googleService;

    @Override
    public void send(String email, String restPassword) {

        try {
            awsSesService.send(SUBJECT, CREATED_RESET_PASSWORD + restPassword, List.of(email));

        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            try {
                googleService.send(SUBJECT, CREATED_RESET_PASSWORD + restPassword, List.of(email));
            } catch (MessagingException ex) {
                log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), ex.getMessage());
                throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
            }
        }
    }
}
