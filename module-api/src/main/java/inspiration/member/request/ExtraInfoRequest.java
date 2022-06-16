package inspiration.member.request;

import inspiration.member.AgeGroupType;
import inspiration.member.GenderType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtraInfoRequest {
    private Long memberId;

    @ApiModelProperty( notes = "F | M | N")
    private GenderType gender;

    @ApiModelProperty( notes = "UNDER_20S | EARLY_20S | LATE_20S | EARLY_30S | OLDER_35")
    private AgeGroupType age;

    private String job;
}
