package inspiration.domain.tag.request;

import inspiration.domain.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@ApiModel("Sample Request")
@Data
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
