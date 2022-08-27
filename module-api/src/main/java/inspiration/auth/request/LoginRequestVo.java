package inspiration.auth.request;

import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class LoginRequestVo {
    String email;
    String password;
}
