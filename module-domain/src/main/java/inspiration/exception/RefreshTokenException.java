package inspiration.exception;

import inspiration.enumeration.ExceptionType;

public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException() {
        super(ExceptionType.EXPIRED_REFRESH_TOKEN.getMessage());
    }

    public RefreshTokenException(String message) {
        super(message);
    }
}