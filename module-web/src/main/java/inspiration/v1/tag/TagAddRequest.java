package inspiration.v1.tag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TagAddRequest {

    @ApiModelProperty(example = "태그등록 테스트", value = "태그등록 테스트")
    @Size(max = 100)
    @NotNull
    private String content;
}
