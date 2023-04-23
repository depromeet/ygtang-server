package inspiration.domain.emailauth;

import inspiration.aws.AwsSesService;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.infrastructure.mail.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordForAuthSendService implements EmailSendService {

    private final MailProperties mailProperties;
    private final static String SUBJECT = "비밀번호 초기화를 위한 이메일 인증";
    private final static String OPEN_HREF = "<a href=";
    private final static String CLOSE_HREF = "></a>";
    private final AwsSesService awsSesService;

    @Override
    public void send(String email, String authToken) {

        try {

            awsSesService.send(SUBJECT, OPEN_HREF + mailProperties.getResetPasswordForAuthSendMail() + email + "&authToken=" + authToken + CLOSE_HREF, List.of(email));

        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
        }
    }
}
