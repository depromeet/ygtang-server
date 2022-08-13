package inspiration.domain.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByMemberId(Long memberId);
    Page<Member> findBy(String email,
                        GenderType genderType,
                        Collection<AgeGroupType> ageGroupTypes,
                        LocalDateTime createdDateTimeFrom,
                        LocalDateTime createdDateTimeTo,
                        Pageable pageable
    );
}
