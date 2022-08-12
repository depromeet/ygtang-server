package inspiration.domain.emailauth;

import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordEmailSendService implements EmailSendService {

    private final JavaMailSender mailSender;
    private final static String SUBJECT = "비빌번호 초기화";
    private final static String CREATED_RESET_PASSWORD = "임시 비밀번호 발급: ";

    @Override
    public void send(String email, String restPassword) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(SUBJECT);
            simpleMailMessage.setText(CREATED_RESET_PASSWORD + restPassword);

            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
        }
    }
}
