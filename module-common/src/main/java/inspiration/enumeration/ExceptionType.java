package inspiration.enumeration;

public enum ExceptionType {

    POST_NOT_FOUND("존재하지 않는 리소스입니다."),

    EMAIl_NOT_FOUND("가입되지 않은 이메일입니다."),

    EXISTS_RESOURCE("이미 존재하는 리소스입니다."),

    MEMBER_NOT_FOUND("존재하지 않는 사용자입니다."),

    EXISTS_EMAIL("이미 존재하는 이메일입니다."),

    EXISTS_NICKNAME("이미 존재하는 닉네임입니다."),

    PASSWORD_NOT_MATCHED("일치하지 않는 패스워드 입니다."),

    LOGIN_NOT_AUTHENTICATED("인증되지 않은 이메일입니다."),

    EMAIL_NOT_AUTHENTICATED("인증되지 않은 이메일입니다."),

    EMAIL_ALREADY_AUTHENTICATED("이미 인증된 이메일 입니다."),

    EMAIL_AUTHENTICATED_TIME_HAS_EXPIRED("이메일 인증 링크의 유효시간이 만료되었습니다."),

    FAILED_TO_SEND_MAIL("메일 전송에 실패하였습니다."),

    USER_NOT_EXISTS("존재하지 않는 유저입니다."),

    EXPIRED_REFRESH_TOKEN("이미 만료된 리프레시 토큰 입니다."),

    VALID_NOT_REFRESH_TOKEN("리프레시 토큰이 일치하지 않습니다."),

    RESOURCE_NOT_FOUND("해당 리소스를 찾을 수 없습니다."),

    INVALID_TOKEN("토큰이 유효하지 않습니다."),

    INVALID_MEMBER("유효한 사용자가 아닙니다."),

    NO_ACCESS_AUTHORIZATION("해당 리소스에 접근 권한이 없습니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
