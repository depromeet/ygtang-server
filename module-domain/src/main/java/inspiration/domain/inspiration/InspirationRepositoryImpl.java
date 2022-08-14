package inspiration.domain.inspiration;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import inspiration.domain.inspiration_tag.QInspirationTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

public class InspirationRepositoryImpl extends QuerydslRepositorySupport implements InspirationRepositoryCustom {
    private final QInspiration qInspiration = QInspiration.inspiration;
    private final QInspirationTag qInspirationTag = QInspirationTag.inspirationTag;

    public InspirationRepositoryImpl() {
        super(Inspiration.class);
    }

    @Override
    public Page<Inspiration> findDistinctByMemberIdAndTagIdInAndTypeAndCreatedDateTimeBetween(
            Long memberId,
            Collection<Long> tagIds,
            Collection<InspirationType> inspirationTypes,
            LocalDateTime createdDateTimeFrom,
            LocalDateTime createdDateTimeTo,
            Pageable pageable
    ) {
        BooleanExpression expression = qInspiration.member.id.eq(memberId);
        if (!CollectionUtils.isEmpty(tagIds)) {
            expression = expression.and(qInspirationTag.tag.id.in(tagIds));
        }
        if (!CollectionUtils.isEmpty(inspirationTypes)) {
            expression = expression.and(qInspiration.type.in(inspirationTypes));
        }
        if (createdDateTimeFrom != null) {
            expression = expression.and(qInspiration.createdDateTime.goe(createdDateTimeFrom));
        }
        if (createdDateTimeTo != null) {
            expression = expression.and(qInspiration.createdDateTime.lt(createdDateTimeTo));
        }

        JPQLQuery<Inspiration> query = from(qInspiration)
                                               .innerJoin(qInspiration.inspirationTags, qInspirationTag)
                                               .where(expression)
                                               .distinct();
        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);
        QueryResults<Inspiration> inspirationQueryResults = query.fetchResults();
        return new PageImpl<>(
                inspirationQueryResults.getResults(),
                pageable,
                inspirationQueryResults.getTotal()
        );
    }
}
