package inspiration.domain.inspiration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collection;

public interface InspirationRepositoryCustom {
    Page<Inspiration> findDistinctByMemberIdAndTagIdInAndTypeAndCreatedDateTimeBetween(
            Long memberId,
            Collection<Long> tagIds,
            Collection<InspirationType> inspirationTypes,
            LocalDate createdDateTimeFrom,
            LocalDate createdDateTimeTo,
            Pageable pageable
    );
}
