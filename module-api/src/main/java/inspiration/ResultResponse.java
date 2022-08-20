package inspiration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse<T> {

    private String message;
    private T data;

    private ResultResponse(String message) {
        this.message = message;
    }

    private ResultResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static ResultResponse<?> from(String message) {

        return new ResultResponse<>(message);
    }

    public static <T> ResultResponse<T> of(String message, T data) {

        return new ResultResponse<T>(message, data);
    }
}
