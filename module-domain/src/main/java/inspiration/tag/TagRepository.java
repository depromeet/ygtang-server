package inspiration.tag;

import inspiration.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Long>{

    Optional<Tag> findById(Long id);
    Page<Tag> findAllByMember(Member member, Pageable pageable);

    Page<Tag> findAllByMemberAndContentContaining(Member member, String content, Pageable pageable);

    Page<Tag> findAllByMemberAndContent(Member member, String content, Pageable pageable);

    List<Tag> findByMember(Member member);

    Optional<Tag> findAllByMemberAndContent(Member member, String content);
}
