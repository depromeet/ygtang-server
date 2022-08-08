package inspiration.v1.advice;

import inspiration.exception.*;
import inspiration.v1.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            ResourceNotFoundException.class
    })
    protected ResultResponse handleBadRequestException(Exception e) {
        log.warn("BAD_REQUEST", e);
        return ResultResponse.from(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResultResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValid", e);
        return ResultResponse.from(
                Optional.of(e.getBindingResult())
                        .map(Errors::getFieldError)
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElseGet(e::getMessage)
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            EmailNotAuthenticatedException.class,
            EmailAuthenticatedTimeExpiredException.class,
            UnauthorizedAccessRequestException.class,
            InvalidTokenException.class,
            NoAccessAuthorizationException.class,
            RefreshTokenException.class,
    })
    public ResultResponse handleUnauthorizedException(Exception e) {
        log.warn("UNAUTHORIZED", e);
        return ResultResponse.from(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostNotFoundException.class)
    protected ResultResponse handleNotFoundException(PostNotFoundException e) {
        log.warn("NOT_FOUND", e);
        return ResultResponse.from(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictRequestException.class)
    protected ResultResponse handleConflictRequestException(ConflictRequestException e) {
        log.warn("CONFLICT", e);
        return ResultResponse.from(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ResultResponse handleException(Exception e) {
        log.error("INTERNAL_SERVER_ERROR", e);
        return ResultResponse.from("서버 에러가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
