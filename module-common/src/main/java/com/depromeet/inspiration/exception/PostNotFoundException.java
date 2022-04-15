package com.depromeet.inspiration.exception;

import com.depromeet.inspiration.enumeration.ExceptionType;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException() {
        super(ExceptionType.POST_NOT_FOUND.getMessage());
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
