package inspiration.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        String token = jwtProvider.resolveToken((HttpServletRequest) request);

        log.info("[Verifying token]");
        log.info(((HttpServletRequest) request).getRequestURL().toString());

        if (token != null && jwtProvider.validationToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }
        // 현재는 ROLE에 대한 처리가 없으므로, AuthenticationException이 발생할 것을 생각하지 않음
        // 위에서 token 값 검증예외는 401 에러가 적합하다고 판단하여 아래의 처리로 필터 단에서 401 exception
        if(Arrays.stream(SecurityConfiguration.ALLOWED_URI_PATTERN)
                    .noneMatch(pattern -> ((HttpServletRequest) request).getRequestURI()
                    .startsWith(pattern))){
            ((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
        }

    }
}