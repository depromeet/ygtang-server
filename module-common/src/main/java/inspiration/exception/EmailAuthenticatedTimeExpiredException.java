package inspiration.exception;

import inspiration.enumeration.ExceptionType;

public class EmailAuthenticatedTimeExpiredException extends RuntimeException {

    public EmailAuthenticatedTimeExpiredException() {
        super(ExceptionType.EMAIL_AUTHENTICATED_TIME_HAS_EXPIRED.getMessage());
    }

    public EmailAuthenticatedTimeExpiredException(String message) {
        super(message);
    }
}
