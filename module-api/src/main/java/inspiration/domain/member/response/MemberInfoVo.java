package inspiration.domain.member.response;

import inspiration.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("ClassCanBeRecord")
public class MemberInfoVo {
    String nickName;
    String email;

    public static MemberInfoVo from(Member member) {
        return new MemberInfoVo(
                member.getNickname(),
                member.getEmail()
        );
    }
}
