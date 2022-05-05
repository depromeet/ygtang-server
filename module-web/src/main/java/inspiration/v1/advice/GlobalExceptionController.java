package inspiration.v1.advice;

import inspiration.exception.*;
import inspiration.v1.ResultResponse;
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
    protected ResultResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResultResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResultResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostNotFoundException.class)
    protected ResultResponse handleNotFoundException(PostNotFoundException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(EmailNotAuthenticatedException.class)
    private ResultResponse handleEmailNotAuthenticatedException(EmailNotAuthenticatedException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(EmailAuthenticatedTimeExpiredException.class)
    private ResultResponse handleEmailAuthenticatedTimeExpiredException(EmailAuthenticatedTimeExpiredException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedAccessRequestException.class)
    private ResultResponse handleUnauthorizedAccessRequestException(UnauthorizedAccessRequestException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    private ResultResponse handleInvalidTokenException(InvalidTokenException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NoAccessAuthorizationException.class)
    private ResultResponse handleNoAccessAuthorizationException(NoAccessAuthorizationException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictRequestException.class)
    private ResultResponse handleConflictRequestException(ConflictRequestException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(RefreshTokenException.class)
    private ResultResponse handleRefreshTokenException(RefreshTokenException exception) {
        String message = exception.getMessage();
        log.debug(message, exception);

        return ResultResponse.from(message);
    }
}
