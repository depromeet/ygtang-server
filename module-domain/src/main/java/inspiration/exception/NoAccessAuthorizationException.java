package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class NoAccessAuthorizationException extends RuntimeException {

    public NoAccessAuthorizationException() {
        super(ExceptionType.NO_ACCESS_AUTHORIZATION.getMessage());
    }

    public NoAccessAuthorizationException(String message) {
        super(message);
    }
}
