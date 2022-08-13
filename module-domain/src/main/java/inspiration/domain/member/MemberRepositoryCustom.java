package inspiration.domain.member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByMemberId(Long memberId);
}
