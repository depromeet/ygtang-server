package inspiration.exception;


import inspiration.enumeration.ExceptionType;

public class InvalidMemberException extends RuntimeException {

    public InvalidMemberException() {
        super(ExceptionType.INVALID_MEMBER.getMessage());
    }

    public InvalidMemberException(String message) {
        super(message);
    }
}
