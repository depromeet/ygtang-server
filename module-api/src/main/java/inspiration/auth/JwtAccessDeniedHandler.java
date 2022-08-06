package inspiration.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)  {
    }
}
