package inspiration.enumeration;

import lombok.Getter;

@Getter
public enum HttpHeaderType {

    HEADER("header");

    private final String message;

    HttpHeaderType(String message) {
        this.message = message;
    }
}
