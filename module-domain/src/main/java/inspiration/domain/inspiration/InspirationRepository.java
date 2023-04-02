package inspiration.domain.inspiration;

import inspiration.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface InspirationRepository extends JpaRepository<Inspiration, Long>, InspirationRepositoryCustom {

    Optional<Inspiration> findById(Long id);

    Page<Inspiration> findAllByMember(Member member, Pageable pageable);

    Optional<Inspiration> findAllByMemberAndId(Member member, Long id);

    List<Inspiration> findAllByMember(Member member);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from Inspiration i where i.member = :member")
    void deleteAllByMember(@Param("member") Member member);
}
