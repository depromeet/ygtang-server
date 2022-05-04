package inspiration.v1;

import lombok.Getter;

@Getter
public class ResultResponse {

    private String message;
    private Object data;

    private ResultResponse(Object data) {
        this.data = data;
    }

    public static ResultResponse from(Object data) {
        return new ResultResponse(data);
    }
}
