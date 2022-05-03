package inspiration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse {

    private String message;
    private Object data;

    private ResultResponse(String message) {
        this.message = message;
    }

    private ResultResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public static ResultResponse of(String message) {

        return new ResultResponse(message);
    }

    public static ResultResponse of(String message, Object data) {

        return new ResultResponse(message, data);
    }
}
