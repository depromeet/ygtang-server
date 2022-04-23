package inspiration.exception;

import inspiration.enumeration.ExceptionType;

public class EmailNotAuthenticatedException extends RuntimeException {

    public EmailNotAuthenticatedException() {
        super(ExceptionType.EMAIL_NOT_AUTHENTICATED.getMessage());
    }

    public EmailNotAuthenticatedException(String message) {
        super(message);
    }
}