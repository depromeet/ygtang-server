package inspiration.domain.inspiration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;

public interface InspirationRepositoryCustom {
    Page<Inspiration> findBy(
            Collection<Long> tagIds,
            Collection<InspirationType> inspirationTypes,
            LocalDateTime createdDateTimeFrom,
            LocalDateTime createdDateTimeTo,
            Pageable pageable
    );
}
