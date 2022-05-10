package inspiration.inspiration;

import inspiration.inspiration_tag.InspirationTag;
import inspiration.member.Member;
import inspiration.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface InspirationRepository extends JpaRepository<Inspiration, Long>{

    Page<Inspiration> findAllByIsDeletedAndMember(boolean isDeleted, Member member, Pageable pageable);


    @Query(value = "select distinct it.inspiration from InspirationTag it where it.tag in :tags")
    Optional<List<Inspiration>> findDistinctInspirationByTagIn(@Param("tags") List<Tag> tags);

    Page<Inspiration> findAllByIsDeletedAndIdIn(boolean isDeleted, List<Long> inspirationIds, Pageable pageable);
}
