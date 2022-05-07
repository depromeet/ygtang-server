package inspiration.inspiration;

import inspiration.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface InspirationRepository extends JpaRepository<Inspiration, Long>{

    @Query(value = "select i from Inspiration i")
    Page<Inspiration> findAllByIsDeletedAndMember(boolean isDeleted, Member member, Pageable pageable);
}
