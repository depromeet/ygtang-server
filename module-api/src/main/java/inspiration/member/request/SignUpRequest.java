package inspiration.member.request;

import inspiration.member.Member;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequest {

    private String email;

    private String nickName;

    private String password;

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
