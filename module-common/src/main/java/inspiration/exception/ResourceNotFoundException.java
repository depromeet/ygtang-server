package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super(ExceptionType.RESOURCE_NOT_FOUND.getMessage());
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
