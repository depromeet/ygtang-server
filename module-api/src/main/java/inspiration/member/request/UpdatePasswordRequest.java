package inspiration.member.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePasswordRequest {

    private String password;

    private String confirmPassword;
}
