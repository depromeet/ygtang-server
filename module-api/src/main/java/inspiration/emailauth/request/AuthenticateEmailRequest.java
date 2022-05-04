package inspiration.emailauth.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AuthenticateEmailRequest {

    private String email;
}
