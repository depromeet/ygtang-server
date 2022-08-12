package inspiration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.ResultResponse;
import inspiration.auth.jwt.JwtPreAuthenticatedProcessingFilter;
import inspiration.auth.jwt.JwtAuthenticationProvider;
import inspiration.auth.jwt.JwtProvider;
import inspiration.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    public static final String[] ALLOWED_URI_PATTERN = new String[]{
            "/api/v1/signup/**",
            "/api/v1/auth/**",
            "/api/v1/reissue",
            "/api/v1/members/sends-email/reset-passwords",
    };
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()

                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            log.warn("UNAUTHORIZED", authException);
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            apiResponseObjectMapper().writeValue(
                                    response.getOutputStream(),
                                    ResultResponse.from("인증이 필요한 요청입니다.")
                            );
                        }
                )
                .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                            log.warn("FORBIDDEN", accessDeniedException);
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            apiResponseObjectMapper().writeValue(
                                    response.getOutputStream(),
                                    ResultResponse.from("접근 권한이 없습니다.")
                            );
                        }
                )

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, ALLOWED_URI_PATTERN).permitAll()
                .antMatchers(HttpMethod.GET, ALLOWED_URI_PATTERN).permitAll()
                .antMatchers(HttpMethod.DELETE, ALLOWED_URI_PATTERN).permitAll()
                .antMatchers(HttpMethod.PUT, ALLOWED_URI_PATTERN).permitAll()
                .antMatchers(HttpMethod.PATCH, ALLOWED_URI_PATTERN).permitAll()
                .anyRequest().hasRole("USER")

                .and()
                .addFilterAt(jwtAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger/**",
                "/health"
        );
    }

    @Bean
    public JwtPreAuthenticatedProcessingFilter jwtAuthenticationFilter() {
        JwtPreAuthenticatedProcessingFilter filter = new JwtPreAuthenticatedProcessingFilter();
        filter.setAuthenticationManager(new ProviderManager(new JwtAuthenticationProvider(jwtProvider, memberService)));
        return filter;
    }

    @Bean
    public ObjectMapper apiResponseObjectMapper() {
        return new ObjectMapper();
    }

}
