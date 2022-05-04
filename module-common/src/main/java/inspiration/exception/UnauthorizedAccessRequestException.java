package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class UnauthorizedAccessRequestException extends RuntimeException {

    public UnauthorizedAccessRequestException() {
        super(ExceptionType.LOGIN_NOT_AUTHENTICATED.getMessage());
    }

    public UnauthorizedAccessRequestException(String message) {
        super(message);
    }
}
