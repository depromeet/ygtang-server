package inspiration.emailauth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_auth_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isAuth;

    @Builder
    public EmailAuth(String email, Boolean auth, String authToken, Boolean expired) {
        this.email = email;
        this.isAuth = auth;
    }
}
