package inspiration.passwordauth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordAuthRepository extends JpaRepository<PasswordAuth, Long> {
    Optional<PasswordAuth> findByEmail(String email);

    boolean existsByEmail(String email);
}
