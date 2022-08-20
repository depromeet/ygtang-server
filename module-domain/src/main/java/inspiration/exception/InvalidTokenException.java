package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super(ExceptionType.INVALID_TOKEN.getMessage());
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
