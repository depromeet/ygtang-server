package inspiration.tag;

import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import inspiration.member.Member;
import inspiration.member.MemberService;
import inspiration.tag.request.TagAddRequest;
import inspiration.tag.response.TagResponse;
import lombok.RequiredArgsConstructor;
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
    public Page<TagResponse> findTags(Pageable pageable, Long memberId) {

        Member member = memberService.findById(memberId);

        Page<Tag> tagPage = tagRepository.findAllByMember(member, pageable);
        return tagPage.map(TagResponse::from);
    }

    public Long addTag(TagAddRequest request, Long memberId) {

        Member member = memberService.findById(memberId);

        Tag tag = request.toEntity();
        tag.writeBy(member);
        return tagRepository.save(tag).getId();
    }

    public void removeTag(Long id, Long memberId) {
        Tag tag = tagRepository.findById(id)
                                .orElseThrow(ResourceNotFoundException::new);

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        tagRepository.delete(tag);
    }
}
