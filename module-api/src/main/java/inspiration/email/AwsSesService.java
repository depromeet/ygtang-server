package inspiration.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsSesService implements EmailService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    private static final String FROM_EMAIL = "yeonggamt@gmail.com";

    //이메일 전송하기
    public void send(String subject, String content, List<String> receivers) {

        Destination destination = new Destination().withToAddresses(receivers);
        Message message = new Message().withSubject(createContent(subject)).withBody(new Body().withHtml(createContent(content)));
        amazonSimpleEmailService.sendEmail(new SendEmailRequest().withSource(FROM_EMAIL).withDestination(destination).withMessage(message));
    }

    private Content createContent(String text) {
        return new Content().withCharset("UTF-8").withData(text);
    }
}
