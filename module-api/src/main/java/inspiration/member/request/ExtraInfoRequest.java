package inspiration.member.request;

import inspiration.member.AgeGroupType;
import inspiration.member.GenderType;
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
    @NotNull(message = "나이는 필수 입력입니다.")
    private AgeGroupType age;

    @NotNull(message = "관심 직무는 필수 입력입니다.")
    private String job;
}
