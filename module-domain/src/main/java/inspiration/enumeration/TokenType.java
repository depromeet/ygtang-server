package inspiration.enumeration;

import lombok.Getter;

@Getter
public enum TokenType {

    JWT("JWT"),
    ACCESS_TOKEN("accessToken"),
    REFRESH_TOKEN("refreshToken"),
    X_AUTH_TOKEN("X-AUTH-TOKEN");


    private final String message;

    TokenType(String message) {
        this.message = message;
    }
    }
