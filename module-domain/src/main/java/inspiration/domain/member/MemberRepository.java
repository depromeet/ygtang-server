package inspiration.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByEmailAndMemberStatus(String email, MemberStatus memberStatus);

    boolean existsByNicknameAndMemberStatus(String nickName, MemberStatus memberStatus);

    boolean existsByEmailAndMemberStatus(String email, MemberStatus memberStatus);

}
