package inspiration.v1.inspiration;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("Sample Request InspirationTagRequest")
@Data
public class InspirationTagRequest {

    @NotNull(message = "영감 id는 필수 입력 입니다.")
    private Long id;
    @NotNull(message = "태그 id는 필수 입력 입니다.")
    private Long tagId;

}
