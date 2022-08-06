package inspiration.tag.request;

import inspiration.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@ApiModel("Sample Request")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagAddRequest {

    @ApiModelProperty(example = "태그등록 테스트", value = "태그등록 테스트")
    @Size(max = 100)
    @NotNull
    private String content;

    public Tag toEntity() {
        return Tag.builder()
                .content(this.content)
                .build();
    }

}
