package inspiration.email;

import javax.mail.MessagingException;
import java.util.List;

public interface EmailService {

    public void send(String subject, String content, List<String> receivers) throws MessagingException;
}
