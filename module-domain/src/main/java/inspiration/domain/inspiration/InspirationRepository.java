package inspiration.domain.inspiration;

import inspiration.domain.member.Member;
import inspiration.domain.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface InspirationRepository extends JpaRepository<Inspiration, Long> {

    Optional<Inspiration> findById(Long id);

    Page<Inspiration> findAllByMember(Member member, Pageable pageable);

    Optional<Inspiration> findAllByMemberAndId(Member member, Long id);

    @Query(value = "select it.inspiration from InspirationTag it where it.tag in :tags group by it.inspiration having count(it.inspiration) >= :count")
    Optional<List<Inspiration>> findDistinctInspirationByTags(@Param("tags") List<Tag> tags, @Param("count") Long count);

    Page<Inspiration> findAllByIdIn(List<Long> inspirationIds, Pageable pageable);

    List<Inspiration> findAllByMember(Member member);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from Inspiration i where i.member = :member")
    void deleteAllByMember(Member member);
}
