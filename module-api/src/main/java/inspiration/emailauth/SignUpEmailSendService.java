package inspiration.emailauth;

import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.property.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpEmailSendService implements EmailSendService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final static String SUBJECT = "이메일 인증";
    private final TemplateEngine templateEngine;

    @Override
    public void send(String email, String authToken) {

        String link = mailProperties.getSignUpEmailSendMail() + email + mailProperties.getAuthToken() + authToken;

        try {
            MimeMessage simpleMailMessage = mailSender.createMimeMessage();

            simpleMailMessage.addRecipients(MimeMessage.RecipientType.TO, email);
            simpleMailMessage.setSubject(SUBJECT);
            simpleMailMessage.setText(setContext(link), "utf-8", "html");

            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
        }
    }

    private String setContext(String link) {
        Context context = new Context();
        context.setVariable("code", link);
        return templateEngine.process("", context);
    }

}
