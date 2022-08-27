package inspiration.v1.tag;

import inspiration.domain.tag.request.TagAddRequestVo;
import inspiration.domain.tag.response.TagResponseVo;
import inspiration.v1.member.MemberAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class TagAssembler {
    private final MemberAssembler memberAssembler;

    public TagAddRequestVo toTagAddRequestVo(TagAddRequest tagAddRequest) {
        return new TagAddRequestVo(
                tagAddRequest.getContent()
        );
    }

    public TagResponse toTagResponse(TagResponseVo tagResponseVo) {
        return new TagResponse(
                tagResponseVo.getId(),
                memberAssembler.toMemberResponse(tagResponseVo.getMemberResponseVo()),
                tagResponseVo.getContent(),
                tagResponseVo.getCreatedDatetime(),
                tagResponseVo.getUpdatedDatetime()
        );
    }
}
