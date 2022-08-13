package inspiration.domain.member.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NicknameResponse {

    String message;

    public static NicknameResponse of() {

        return new NicknameResponse("사용 가능한 닉네임 입니다.");
    }
}
