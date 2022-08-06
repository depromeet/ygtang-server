package inspiration.emailauth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
