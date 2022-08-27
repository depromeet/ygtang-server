package inspiration.domain.member.response;

import inspiration.domain.member.Member;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class MemberResponseVo {
    Long id;
    String nickName;
    String email;
    LocalDateTime createdDateTime;

    public static MemberResponseVo of(Member member) {
        return new MemberResponseVo(
                member.getId(),
                member.getNickname(),
                member.getEmail(),
                member.getCreatedDateTime()
        );
    }
}
