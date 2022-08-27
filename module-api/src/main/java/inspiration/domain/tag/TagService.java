package inspiration.domain.tag;

import inspiration.domain.member.Member;
import inspiration.domain.member.MemberService;
import inspiration.domain.tag.request.TagAddRequestVo;
import inspiration.domain.tag.response.TagResponseVo;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@SuppressWarnings("ClassCanBeRecord")
public class TagService {

    private final TagRepository tagRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    @Cacheable(value = "tag", key = "{#memberId, #pageable.pageNumber, #pageable.pageSize}")
    public Page<TagResponseVo> findTags(Pageable pageable, Long memberId) {
        Member member = memberService.findById(memberId);
        return tagRepository.findAllByMember(member, pageable)
                            .map(TagResponseVo::from);
    }

    @Transactional(readOnly = true)
    public Page<TagResponseVo> indexTags(Pageable pageable, String keyword, Long memberId) {
        Member member = memberService.findById(memberId);
        return tagRepository.findAllByMemberAndContentContaining(member, keyword, pageable)
                            .map(TagResponseVo::from);
    }

    @Transactional(readOnly = true)
    public Page<TagResponseVo> searchTags(Pageable pageable, String keyword, Long memberId) {
        Member member = memberService.findById(memberId);
        return tagRepository.findAllByMemberAndContent(member, keyword, pageable)
                            .map(TagResponseVo::from);
    }

    @CacheEvict(value = "tag", allEntries = true)
    public TagResponseVo addTag(TagAddRequestVo requestVo, Long memberId) {
        Member member = memberService.findById(memberId);
        if (tagRepository.findAllByMemberAndContent(member, requestVo.getContent()).isPresent()) {
            throw new ConflictRequestException();
        }
        Tag tag = requestVo.toEntity();
        tag.writeBy(member);
        Tag savedTag = tagRepository.save(tag);
        return TagResponseVo.from(savedTag);
    }

    public Tag getTag(Long id) {
        return tagRepository.findById(id)
                            .orElseThrow(ResourceNotFoundException::new);
    }

    @CacheEvict(value = "tag", allEntries = true)
    public void removeTag(Long id, Long memberId) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(ResourceNotFoundException::new);

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        tagRepository.delete(tag);
    }

    @CacheEvict(value = "tag", allEntries = true)
    public void removeAllTag(Long memberId) {
        Member member = memberService.findById(memberId);

        List<Tag> tags = tagRepository.findByMember(member);

        tagRepository.deleteAll(tags);
    }
}
