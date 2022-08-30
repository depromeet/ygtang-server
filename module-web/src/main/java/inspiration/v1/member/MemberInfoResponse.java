package inspiration.v1.member;

import lombok.Data;

@Data
@SuppressWarnings("ClassCanBeRecord")
public class MemberInfoResponse {
    private final String nickName;
    private final String email;
}
