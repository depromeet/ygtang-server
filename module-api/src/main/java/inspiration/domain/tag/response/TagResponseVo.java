package inspiration.domain.tag.response;

import inspiration.domain.member.response.MemberResponseVo;
import inspiration.domain.tag.Tag;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class TagResponseVo {
    Long id;
    MemberResponseVo memberResponseVo;
    String content;
    LocalDateTime createdDatetime;
    LocalDateTime updatedDatetime;

    public static TagResponseVo from(Tag tag) {
        return new TagResponseVo(
                tag.getId(),
                MemberResponseVo.of(tag.getMember()),
                tag.getContent(),
                tag.getCreatedDateTime(),
                tag.getUpdatedDateTime()
        );
    }
}
