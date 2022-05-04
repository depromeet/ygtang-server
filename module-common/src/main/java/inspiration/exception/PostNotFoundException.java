package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException() {
        super(ExceptionType.POST_NOT_FOUND.getMessage());
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
