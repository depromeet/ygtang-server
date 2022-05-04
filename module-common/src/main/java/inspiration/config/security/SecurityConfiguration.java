package inspiration.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtProvider jwtProvider;
    public static final String [] ALLOWED_URI_PATTERN = {"/api/v1/members"};

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()

                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, addMatchers(ALLOWED_URI_PATTERN)).permitAll()
                .antMatchers(HttpMethod.GET, addMatchers(ALLOWED_URI_PATTERN)).permitAll()
                .antMatchers(HttpMethod.DELETE, addMatchers(ALLOWED_URI_PATTERN)).permitAll()
                .antMatchers(HttpMethod.POST, addMatchers(ALLOWED_URI_PATTERN)).permitAll()
                .antMatchers(HttpMethod.PUT, addMatchers(ALLOWED_URI_PATTERN)).permitAll()
                .anyRequest().hasRole("USER")


                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**");
    }
    private String[] addMatchers(String[] patterns) {
        return Arrays.stream(patterns).map(pattern -> pattern.concat("/**")).collect(Collectors.toList()).toArray(String[]::new);
    }

}
