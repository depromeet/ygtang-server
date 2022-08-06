package inspiration.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    private final String host;
    private final int port;
    private final String userName;
    private final String password;
    private final String signUpEmailSendMail;
    private final String resetPasswordForAuthSendMail;
    private final String authToken;
}
