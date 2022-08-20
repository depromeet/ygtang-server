package inspiration.domain.inspiration_tag;

import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface InspirationTagRepository extends JpaRepository<InspirationTag, Long>{

    Optional<InspirationTag> findByInspirationAndTag(Inspiration inspiration, Tag tag);

    void deleteAllByInspiration(Inspiration inspiration);

    @Modifying
    @Query(value = "delete from InspirationTag where inspiration in :inspirations")
    void deleteAllByInspirationIn(List<Inspiration> inspirations);

    void deleteAllByTag(Tag tag);

}
