package inspiration.domain.member.response;

import inspiration.domain.member.Member;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberInfoResponse {

    private String nickName;
    private String email;

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .nickName(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
