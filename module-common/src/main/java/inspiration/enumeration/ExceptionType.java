package inspiration.enumeration;

public enum ExceptionType {

    POST_NOT_FOUND("존재하지 않는 리소스입니다."),
    EMAIL_NOT_AUTHENTICATED("인증되지 않은 이메일입니다."),
    EMAIL_ALREADY_AUTHENTICATED("이미 인증된 이메일 입니다."),
    EMAIL_AUTHENTICATED_TIME_HAS_EXPIRED("이메일 인증 링크의 유효시간이 만료되었습니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
