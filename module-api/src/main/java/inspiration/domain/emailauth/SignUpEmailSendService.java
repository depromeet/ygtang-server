package inspiration.domain.emailauth;

import inspiration.email.AwsSesService;
import inspiration.email.GoogleService;
import inspiration.enumeration.ExceptionType;
import inspiration.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.MessagingException;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpEmailSendService implements EmailSendService {


    private final static String SUBJECT = "이메일 인증";
    private final AwsSesService awsSesService;
    private final GoogleService googleService;

    @Value("${ygtang.server.scheme}")
    private String scheme;
    @Value("${ygtang.server.host}")
    private String host;
    @Value("${ygtang.server.port}")
    private String port;


    @Override
    public void send(String email, String authToken) {

        String link = UriComponentsBuilder.newInstance()
                                          .scheme(scheme)
                                          .host(host)
                                          .port(port)
                                          .path("/api/v1/auth/email/signup")
                                          .queryParam("email", URLEncoder.encode(email, StandardCharsets.UTF_8))
                                          .queryParam("authToken", authToken)
                                          .build(false)
                                          .toUriString();

        try {
            awsSesService.send(SUBJECT, setHtml(link), List.of(email));
        } catch (Exception e) {
            log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), e.getMessage());
            try {
                googleService.send(SUBJECT, setHtml(link), List.of(email));
            } catch (MessagingException ex) {
                log.debug(ExceptionType.FAILED_TO_SEND_MAIL.getMessage(), ex.getMessage());
                throw new PostNotFoundException(ExceptionType.FAILED_TO_SEND_MAIL.getMessage());
            }
        }
    }

    private String setHtml(String link) {
        StringBuilder builder = new StringBuilder();

        builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>ygtang email verification</title>\n" +
                "  </head>\n" +
                "  <body style=\"margin: 0; padding: 0\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "      <tr>\n" +
                "        <td style=\"height: 80px; padding-bottom: 36px\">\n" +
                "          <img\n" +
                "            src=\"https://user-images.githubusercontent.com/26461307/171172952-92151475-8487-4dd7-9670-092a3cafd14f.png\"\n" +
                "            alt=\"ygtang text logo\"\n" +
                "            style=\"width: 100%; height: 100%; object-fit: contain\"\n" +
                "          />\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td style=\"width: 100%; height: 150px; padding-bottom: 50px\">\n" +
                "          <img\n" +
                "            src=\"https://user-images.githubusercontent.com/26461307/171172966-42f47ec4-42c6-416d-ac12-bf26e2e2116f.png\"\n" +
                "            alt=\"ygtang logo\"\n" +
                "            style=\"width: 100%; height: 100%; object-fit: contain\"\n" +
                "          />\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td\n" +
                "          align=\"center\"\n" +
                "          style=\"text-align: center; padding-bottom: 50px; line-height: 150%; color: #5a676a\"\n" +
                "        >\n" +
                "          이메일 주소를 인증하면,<br />\n" +
                "          영감을 자유롭게 추가할 수 있어요.<br />\n" +
                "          아래 버튼을 눌러 이메일 주소를 인증해 주세요.<br />\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "\u200B\n" +
                "      <tr>\n" +
                "        <td align=\"center\">\n" +
                "          <a\n" +
                "            style=\"\n" +
                "              cursor: pointer;\n" +
                "              padding: 16px;\n" +
                "              background-color: #f15a24;\n" +
                "              border-radius: 4px;\n" +
                "              text-decoration: none;\n" +
                "            \"\n" +
                "            href=\"" + link + "\"\n" +
                "          >\n" +
                "            <font color=\"#fff\">이메일 인증하기</font>\n" +
                "          </a>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>");

        return builder.toString();
    }
}
