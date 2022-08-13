package inspiration.domain.member;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.Collection;
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

    @Override
    public Page<Member> findBy(
            String email,
            GenderType genderType,
            Collection<AgeGroupType> ageGroupTypes,
            LocalDateTime createdDateTimeFrom,
            LocalDateTime createdDateTimeTo,
            Pageable pageable
    ) {
        BooleanExpression condition = qMember.id.gt(0); // member.id > 0
        if (email != null) {
            condition = condition.and(qMember.email.eq(email));
        }
        if (genderType != null) {
            condition = condition.and(qMember.gender.eq(genderType));
        }
        if (ageGroupTypes != null && !ageGroupTypes.isEmpty()) {
            condition = condition.and(qMember.age_group.in(ageGroupTypes));
        }
        if (createdDateTimeFrom != null) {
            condition = condition.and(qMember.createdDateTime.goe(createdDateTimeFrom));
        }
        if (createdDateTimeTo != null) {
            condition = condition.and(qMember.createdDateTime.loe(createdDateTimeTo));
        }
        QueryResults<Member> memberQueryResults = from(qMember)
                                                          .where(condition)
                                                          .limit(pageable.getPageSize())
                                                          .offset(pageable.getOffset())
                                                          .fetchResults();
        return new PageImpl<>(
                memberQueryResults.getResults(),
                pageable,
                memberQueryResults.getTotal()
        );
    }
}
