package inspiration.inspiration.request;

import inspiration.inspiration.Inspiration;
import inspiration.inspiration.InspirationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@ApiModel("Sample Request InspirationAddRequest")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InspirationAddRequest {

    @ApiModelProperty(example = "LINK", value = "IMAGE | LINK | TEXT", notes = "IMAGE | LINK | TEXT")
    private InspirationType type;
    @ApiModelProperty(example = "영감등록 테스트", value = "LINK, TEXT의 경우 해당 데이터 입력", notes = "LINK, TEXT의 경우 해당 데이터 입력")
    private String content;
    @ApiModelProperty(example = "메모등록 테스트", value = "메모 입력", notes = "메모 입력")
    private String memo;

    public Inspiration toEntity() {
        return Inspiration.builder()
                .content(this.content)
                .type(this.type)
                .memo(this.memo)
                .build();
    }

}
