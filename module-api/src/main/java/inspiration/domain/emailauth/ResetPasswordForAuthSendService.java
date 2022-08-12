package inspiration.domain.emailauth;

import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.infrastructure.mail.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordForAuthSendService implements EmailSendService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final static String SUBJECT = "비밀번호 초기화를 위한 이메일 인증";
    private final static String OPEN_HREF = "<a href=";
    private final static String CLOSE_HREF = "></a>";

    @Override
    public void send(String email, String authToken) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(SUBJECT);
            simpleMailMessage.setText(OPEN_HREF + mailProperties.getResetPasswordForAuthSendMail() + email + mailProperties.getAuthToken() + authToken + CLOSE_HREF);

            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
        }
    }
}
