package inspiration.member.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull @Valid
    private String password;

    @NotNull @Valid @Email
    private String email;
}
