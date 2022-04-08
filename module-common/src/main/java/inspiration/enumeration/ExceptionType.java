package inspiration.enumeration;

public enum ExceptionType {

    POST_NOT_FOUND("존재하지 않는 리소스입니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
