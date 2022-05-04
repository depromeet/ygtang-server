package inspiration.v1.advice;

import inspiration.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostNotFoundException.class)
    protected ErrorResponse handleNotFoundException(PostNotFoundException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(EmailNotAuthenticatedException.class)
    private ErrorResponse handleEmailNotAuthenticatedException(EmailNotAuthenticatedException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(EmailAuthenticatedTimeExpiredException.class)
    private ErrorResponse handleEmailAuthenticatedTimeExpiredException(EmailAuthenticatedTimeExpiredException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedAccessRequestException.class)
    private ErrorResponse handleUnauthorizedAccessRequestException(UnauthorizedAccessRequestException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    private ErrorResponse handleInvalidTokenException(InvalidTokenException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NoAccessAuthorizationException.class)
    private ErrorResponse handleNoAccessAuthorizationException(NoAccessAuthorizationException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictRequestException.class)
    private ErrorResponse handleConflictRequestException(ConflictRequestException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ErrorResponse.of(message);
    }
}
