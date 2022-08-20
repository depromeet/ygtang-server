package inspiration.domain.inspiration_tag;

import inspiration.exception.ResourceNotFoundException;
import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.tag.Tag;
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

    public InspirationTag save(InspirationTag inspirationTag) {
        return inspirationTagRepository.save(inspirationTag);
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
