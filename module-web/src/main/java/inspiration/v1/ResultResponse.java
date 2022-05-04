package inspiration.v1;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse {

    private String message;
    private Object data;

    private ResultResponse(String message) {
        this.message = message;
    }

    public static ResultResponse from(String message) {

        return new ResultResponse(message);
    }
}
