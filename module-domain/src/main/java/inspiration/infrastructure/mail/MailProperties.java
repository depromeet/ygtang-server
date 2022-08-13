package inspiration.infrastructure.mail;

import lombok.Data;

@Data
public class MailProperties {
    private String host;
    private int port;
    private String userName;
    private String password;
    private String signUpEmailSendMail;
    private String resetPasswordForAuthSendMail;
    private String authToken;
}
