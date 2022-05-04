package inspiration.inspiration.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InspirationModifyRequest {

    @NotBlank(message = "영감 id는 필수 입력 입니다.")
    private Long id;

    private String memo;

}
