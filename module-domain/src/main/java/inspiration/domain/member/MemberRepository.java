package inspiration.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByEmailAndStatusIsNull(String email);

    boolean existsByNicknameAndStatusIsNull(String nickName);

    boolean existsByEmailAndStatusIsNull(String email);

}
