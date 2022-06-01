package inspiration.emailauth;

import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import inspiration.property.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpEmailSendService implements EmailSendService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final static String SUBJECT = "이메일 인증";

    @Override
    public void send(String email, String authToken) {

        String link = mailProperties.getSignUpEmailSendMail() + email + mailProperties.getAuthToken() + authToken;

        try {
            MimeMessage simpleMailMessage = mailSender.createMimeMessage();

            simpleMailMessage.addRecipients(MimeMessage.RecipientType.TO, email);
            simpleMailMessage.setSubject(SUBJECT);
            simpleMailMessage.setText(createHtml(link), "utf-8", "html");

            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
        }
    }

    private String createHtml(String link) {

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  </head>\n" +
                "  <body style=\"margin: 0; padding: 0\">\n" +
                "    <main\n" +
                "      style=\"\n" +
                "        width: 600px;\n" +
                "        display: flex;\n" +
                "        flex-direction: column;\n" +
                "        align-items: center;\n" +
                "        padding: 24px 10px;\n" +
                "      \"\n" +
                "    >\n" +
                "      <div style=\"height: 80px; margin-bottom: 36px\">\n" +
                "        <img\n" +
                "          src=\"https://user-images.githubusercontent.com/26461307/171172952-92151475-8487-4dd7-9670-092a3cafd14f.png\"\n" +
                "          alt=\"ygtang text logo\"\n" +
                "          style=\"width: 100%; height: 100%; object-fit: contain\"\n" +
                "        />\n" +
                "      </div>\n" +
                "      <div style=\"width: 100%; height: 150px; margin-bottom: 50px\">\n" +
                "        <img\n" +
                "          src=\"https://user-images.githubusercontent.com/26461307/171172966-42f47ec4-42c6-416d-ac12-bf26e2e2116f.png\"\n" +
                "          alt=\"ygtang logo\"\n" +
                "          style=\"width: 100%; height: 100%; object-fit: contain\"\n" +
                "        />\n" +
                "      </div>\n" +
                "      <p style=\"text-align: center; margin-bottom: 50px; line-height: 150%; color: #5a676a\">\n" +
                "        이메일 주소를 인증하면,<br />\n" +
                "        영감을 자유롭게 추가할 수 있어요.<br />\n" +
                "        아래 버튼을 눌러 이메일 주소를 인증해 주세요.<br />\n" +
                "      </p>");
        sb.append("\n");
        sb.append("<a\n" +
                "          style=\"\n" +
                "          all: unset;\n" +
                "          cursor: pointer;\n" +
                "          padding: 16px;\n" +
                "          background-color: #f15a24;\n" +
                "          border-radius: 4px;\n" +
                "          color: #d6dbdc;\n" +
                "        \"\n" +
                "          href=" + link +
                "  >\n" + "이메일 인증하기" +
                "  </a>"
        );
        sb.append("    </main>\n" +
                "  </body>\n" +
                "</html>");
        return sb.toString();
    }
}
