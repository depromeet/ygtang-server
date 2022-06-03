package inspiration.inspiration_tag;

import inspiration.inspiration.Inspiration;
import inspiration.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface InspirationTagRepository extends JpaRepository<InspirationTag, Long>{

    Optional<InspirationTag> findByInspirationAndTag(Inspiration inspiration, Tag tag);

    Optional<List<InspirationTag>> findInspirationByTagIn(List<Tag> tags);

    void deleteAllByInspiration(Inspiration inspiration);

    void deleteAllByInspirationIn(List<Inspiration> inspirations);

    void deleteAllByTag(Tag tag);

}
