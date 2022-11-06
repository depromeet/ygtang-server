package inspiration.domain.inspiration.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.NotNull;


@Getter
@ApiModel("Sample Request InspirationTagRequest")
@Data
public class InspirationTagRequest {

    @NotNull(message = "영감 id는 필수 입력 입니다.")
    private Long id;
    @NotNull(message = "태그 id는 필수 입력 입니다.")
    private Long tagId;

}
