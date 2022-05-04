package inspiration.member.response;

import inspiration.member.Member;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private Long id;
    private String nickName;
    private String email;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                            .id(member.getId())
                            .nickName(member.getNickname())
                            .email(member.getEmail())
                            .build();
    }
}
