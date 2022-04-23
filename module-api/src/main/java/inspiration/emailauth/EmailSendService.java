package inspiration.emailauth;

import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.property.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class EmailSendService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final static String SUBJECT = "회원가입 이메일 인증";

    @Async
    public void send(String email, String authToken) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(SUBJECT);
            simpleMailMessage.setText(mailProperties.getAuthMail() + email + mailProperties.getAuthToken() + authToken);

            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e);
            throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
        }
    }
}