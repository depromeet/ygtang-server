package inspiration.utils;

import java.util.UUID;

public class AuthTokenUtil {

    public static String AuthToken() {
        return UUID.randomUUID().toString();
    }
}
