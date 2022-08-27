package inspiration.v1.inspiration;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InspirationModifyRequest {

    @NotNull(message = "영감 id는 필수 입력 입니다.")
    private Long id;

    private String memo;

}
