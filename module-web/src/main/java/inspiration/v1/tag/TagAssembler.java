package inspiration.v1.tag;

import inspiration.domain.tag.request.TagAddRequestVo;
import inspiration.domain.tag.response.TagResponseVo;
import inspiration.v1.member.MemberAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        if (tagResponseVo == null) {
            return null;
        }
        return new TagResponse(
                tagResponseVo.getId(),
                Optional.ofNullable(tagResponseVo.getMemberResponseVo())
                        .map(memberAssembler::toMemberResponse)
                        .orElse(null),
                tagResponseVo.getContent(),
                tagResponseVo.getCreatedDatetime(),
                tagResponseVo.getUpdatedDatetime()
        );
    }
}
