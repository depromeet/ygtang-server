package inspiration.utils;

import java.util.UUID;

public class AuthTokenUtil {

    public static String getAuthToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
