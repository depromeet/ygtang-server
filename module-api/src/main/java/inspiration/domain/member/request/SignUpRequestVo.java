package inspiration.domain.member.request;

import inspiration.domain.member.Member;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class SignUpRequestVo {
    String email;
    String nickName;
    String password;
    String confirmPassword;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                     .email(email)
                     .password(passwordEncoder.encode(password))
                     .nickname(nickName)
                     .build();
    }
}
