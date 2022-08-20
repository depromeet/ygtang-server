package inspiration.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickName);

    boolean existsByEmail(String email);
}
