package inspiration.domain.member.request;

import inspiration.domain.member.AgeGroupType;
import inspiration.domain.member.GenderType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtraInfoRequest {

    @ApiModelProperty( notes = "F | M | N")
    @NotNull(message = "성별은 필수 입력입니다.")
    private GenderType gender;

    @ApiModelProperty( notes = "UNDER_20S | EARLY_20S | LATE_20S | EARLY_30S | OLDER_35")
    private AgeGroupType age;

    @NotNull(message = "관심 직무는 필수 입력입니다.")
    private String job;
}
