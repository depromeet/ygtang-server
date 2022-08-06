package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class ConflictRequestException extends RuntimeException {

    public ConflictRequestException() {
        super(ExceptionType.EXISTS_RESOURCE.getMessage());
    }

    public ConflictRequestException(String message) {
        super(message);
    }
}
