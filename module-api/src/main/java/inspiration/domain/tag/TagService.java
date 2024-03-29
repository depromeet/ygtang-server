package inspiration.domain.tag;

import inspiration.RestPage;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberService;
import inspiration.domain.tag.request.TagAddRequest;
import inspiration.domain.tag.response.TagResponse;
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
public class TagService {

    private final TagRepository tagRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
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

    public TagResponse addTag(TagAddRequest request, Long memberId) {

        Member member = memberService.findById(memberId);

        if (tagRepository.findAllByMemberAndContent(member, request.getContent()).isPresent()) {
            throw new ConflictRequestException();
        }
        Tag tag = request.toEntity();
        tag.writeBy(member);

        Tag savedTag = tagRepository.save(tag);
        return TagResponse.from(savedTag);
    }

    public Tag getTag(Long id) {
        return tagRepository.findById(id)
                                .orElseThrow(ResourceNotFoundException::new);
    }

    public void removeTag(Long id, Long memberId) {
        Tag tag = tagRepository.findById(id)
                                .orElseThrow(ResourceNotFoundException::new);

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        tagRepository.delete(tag);
    }

    public void removeAllTag(Long memberId) {
        Member member = memberService.findById(memberId);

        List<Tag> tags = tagRepository.findByMember(member);

        tagRepository.deleteAll(tags);
    }
}
