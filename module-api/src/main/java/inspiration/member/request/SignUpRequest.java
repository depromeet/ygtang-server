package inspiration.member.request;

import inspiration.member.Member;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.*;
import java.util.Collections;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequest {

    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력 입니다.")
    private String email;

    @NotBlank(message = "올바른 닉네임을 입력해주세요.")
    @Size(min = 4, max = 20, message = "닉네임은 4자 이상 20자 이하로 입력해주세요.")
    private String nickName;

    @NotBlank(message = "올바른 비밀번호를 입력해주세요.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "올바른 비밀번호를 입력해주세요.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
    private String confirmPassword;

    @Builder
    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickName)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .nickname(nickName)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }
}
