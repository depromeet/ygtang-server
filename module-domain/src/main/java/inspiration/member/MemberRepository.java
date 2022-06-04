package inspiration.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickName);

    boolean existsByEmail(String email);
}
