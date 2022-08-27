package inspiration.v1.member;

import lombok.Data;
import lombok.Getter;

@Data
@SuppressWarnings("ClassCanBeRecord")
public class MemberInfoResponse {
    private final String nickName;
    private final String email;
}
