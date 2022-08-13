package inspiration.domain.inspiration;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import inspiration.domain.inspiration_tag.QInspirationTag;
import inspiration.domain.tag.QTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.Collection;

public class InspirationRepositoryImpl extends QuerydslRepositorySupport implements InspirationRepositoryCustom {

    private final QInspiration qInspiration = QInspiration.inspiration;
    private final QInspirationTag qInspirationTag = QInspirationTag.inspirationTag;

    public InspirationRepositoryImpl() {
        super(Inspiration.class);
    }


    @Override
    public Page<Inspiration> findBy(
            Collection<Long> tagIds,
            Collection<InspirationType> inspirationTypes,
            LocalDateTime createdDateTimeFrom,
            LocalDateTime createdDateTimeTo,
            Pageable pageable
    ) {
        // FIXME: join, sort
        BooleanExpression condition = qInspiration.id.eq(qInspirationTag.inspiration.id);

        if (!tagIds.isEmpty()) {
            condition = condition.and(qInspirationTag.tag.id.in(tagIds));
        }
        if (!inspirationTypes.isEmpty()) {
            condition = condition.and(qInspiration.type.in(inspirationTypes));
        }
        if (createdDateTimeFrom != null) {
            condition = condition.and(qInspiration.createdDateTime.goe(createdDateTimeFrom));
        }
        if (createdDateTimeTo != null) {
            condition = condition.and(qInspiration.createdDateTime.loe(createdDateTimeTo));
        }

        QueryResults<Inspiration> inspirationQueryResults = from(qInspiration)
                                                                    .where(condition)
                                                                    .limit(pageable.getPageSize())
                                                                    .offset(pageable.getOffset())
                                                                    .fetchResults();

        return new PageImpl<>(
                inspirationQueryResults.getResults(),
                pageable,
                inspirationQueryResults.getTotal()
        );
    }
}
