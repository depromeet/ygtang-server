package inspiration.enumeration;

public enum ExceptionType {

    POST_NOT_FOUND("존재하지 않는 리소스입니다."),

    EMAIl_NOT_FOUND("가입되지 않은 이메일입니다."),

    EXISTS_RESOURCE("이미 존재하는 리소스입니다."),

    EXISTS_EMAIL("이미 존재하는 이메일입니다."),

    EXISTS_NICKNAME("이미 존재하는 닉네임입니다."),

    VALID_NOT_PASSWORD("일치하지 않는 패스워드 입니다."),

    LOGIN_NOT_AUTHENTICATED("인증되지 않은 이메일입니다."),

    EMAIL_NOT_AUTHENTICATED("인증되지 않은 이메일입니다."),

    EMAIL_ALREADY_AUTHENTICATED("이미 인증된 이메일 입니다."),

    EMAIL_AUTHENTICATED_TIME_HAS_EXPIRED("이메일 인증 링크의 유효시간이 만료되었습니다."),

    FAILED_TO_SEND_MAIL("메일 전송에 실패하였습니다."),

    USER_NOT_EXISTS("존재하지 않는 유저입니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
