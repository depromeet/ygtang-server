package inspiration.domain.emailauth;

public interface EmailSendService {
    void send(String email, String authToken);
}
