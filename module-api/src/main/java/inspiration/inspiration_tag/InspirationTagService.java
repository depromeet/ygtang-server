package inspiration.inspiration_tag;

import inspiration.exception.ResourceNotFoundException;
import inspiration.inspiration.Inspiration;
import inspiration.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class InspirationTagService {

    private final InspirationTagRepository inspirationTagRepository;

    @Transactional(readOnly = true)
    public InspirationTag findInspirationTag(Inspiration inspiration, Tag tag) {
        return inspirationTagRepository.findByInspirationAndTag(inspiration, tag)
                                        .orElseThrow(ResourceNotFoundException::new);

    }

    @Transactional(readOnly = true)
    public List<InspirationTag> findInspirationByTags(List<Tag> tags) {
        return inspirationTagRepository.findInspirationByTagIn(tags)
                                         .orElseThrow(ResourceNotFoundException::new);

    }

    public InspirationTag save(InspirationTag inspirationTag) {
        return inspirationTagRepository.save(inspirationTag);
    }

    public void delete(InspirationTag inspirationTag) {
        inspirationTagRepository.delete(inspirationTag);
    }

    public void deleteAllByInspiration(Inspiration inspiration) {
        inspirationTagRepository.deleteAllByInspiration(inspiration);
    }

    public void deleteAllByInspirationIn(List<Inspiration> inspirations) {
        inspirationTagRepository.deleteAllByInspirationIn(inspirations);
    }

    public void deleteAllByTag(Tag tag) {
        inspirationTagRepository.deleteAllByTag(tag);
    }
}
