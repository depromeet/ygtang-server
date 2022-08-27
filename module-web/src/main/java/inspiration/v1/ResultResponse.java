package inspiration.v1;

import inspiration.enumeration.ExceptionType;
import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse<T> {

    private String message;
    private T data;

    private ResultResponse(String message) {
        this.message = message;
    }

    public static ResultResponse<?> from(String message) {
        return new ResultResponse<>(message);
    }

    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>("SUCCESS", data);
    }

    public static <T> ResultResponse<T> of(ExceptionType exceptionType, T data) {
        return new ResultResponse<>(exceptionType.getMessage(), data);
    }
}
