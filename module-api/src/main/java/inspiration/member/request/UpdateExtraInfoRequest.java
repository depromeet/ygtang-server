package inspiration.member.request;

import inspiration.enumeration.AgeType;
import inspiration.enumeration.GenderType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateExtraInfoRequest {
    private GenderType gender;
    private AgeType age;
    private String job;
}
