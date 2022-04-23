package inspiration.emailauth.request;

import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailAuthRequest {

    private String email;

    private String authToken;
}
