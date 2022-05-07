package inspiration.inspiration;

import inspiration.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InspirationRepository extends JpaRepository<Inspiration, Long>{

    Page<Inspiration> findAllByIsDeletedAndMember(boolean isDeleted, Member member, Pageable pageable);
}
