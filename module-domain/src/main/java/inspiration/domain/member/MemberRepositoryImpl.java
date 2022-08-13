package inspiration.domain.member;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {
    private final QMember qMember = QMember.member;

    public MemberRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        return Optional.ofNullable(
                from(qMember)
                        .where(qMember.id.eq(memberId))
                        .fetchOne()
        );
    }
}
