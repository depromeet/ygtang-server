package inspiration.v1;

import inspiration.enumeration.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse<T> {

    private String message;
    private T data;

    public static ResultResponse<Object> from(String message) {
        return new ResultResponse<>(message, null);
    }

    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>("SUCCESS", data);
    }

    public static <T> ResultResponse<T> of(ExceptionType exceptionType, T data) {
        return new ResultResponse<>(exceptionType.getMessage(), data);
    }
}
