package inspiration.domain.emailauth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
