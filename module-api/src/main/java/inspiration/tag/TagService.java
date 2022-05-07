package inspiration.tag;

import inspiration.RestPage;
import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import inspiration.member.Member;
import inspiration.member.MemberService;
import inspiration.tag.request.TagAddRequest;
import inspiration.tag.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    @Cacheable(value = "tag", key = "{#memberId + #pageable.pageNumber}")
    public RestPage<TagResponse> findTags(Pageable pageable, Long memberId) {

        Member member = memberService.findById(memberId);

        Page<Tag> tagPage = tagRepository.findAllByMember(member, pageable);
        return new RestPage<>(tagPage.map(TagResponse::from));
    }

    @Transactional(readOnly = true)
    public RestPage<TagResponse> indexTags(Pageable pageable, String keyword, Long memberId) {

        Member member = memberService.findById(memberId);

        Page<Tag> tagPage = tagRepository.findAllByMemberAndContentContaining(member, keyword, pageable);
        return new RestPage<>(tagPage.map(TagResponse::from));
    }

    @Transactional(readOnly = true)
    public Page<TagResponse> searchTags(Pageable pageable, String keyword, Long memberId) {

        Member member = memberService.findById(memberId);

        Page<Tag> tagPage = tagRepository.findAllByMemberAndContent(member, keyword, pageable);
        return new RestPage<>(tagPage.map(TagResponse::from));
    }

    @CacheEvict(value = "tag", allEntries = true)
    public Long addTag(TagAddRequest request, Long memberId) {

        Member member = memberService.findById(memberId);

        Tag tag = request.toEntity();
        tag.writeBy(member);
        return tagRepository.save(tag).getId();
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
}
