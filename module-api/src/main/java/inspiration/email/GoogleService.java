package inspiration.email;

import inspiration.enumeration.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String subject, String content, List<String> receivers) throws MessagingException {

        MimeMessage simpleMailMessage = mailSender.createMimeMessage();

        for (String emailRecipient : receivers) {
            Address toAddress=new InternetAddress(emailRecipient);
            simpleMailMessage.addRecipient(Message.RecipientType.TO,toAddress);
        }
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content, "utf-8", "html");

        mailSender.send(simpleMailMessage);
    }
}
