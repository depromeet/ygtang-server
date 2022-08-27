package inspiration.domain.member.request;

import inspiration.domain.member.AgeGroupType;
import inspiration.domain.member.GenderType;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class ExtraInfoRequestVo {
    GenderType gender;
    AgeGroupType age;
    String job;
}
