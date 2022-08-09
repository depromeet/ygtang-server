package inspiration.auth.jwt;

import inspiration.exception.UnauthorizedAccessRequestException;
import inspiration.member.Member;
import inspiration.member.MemberService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@SuppressWarnings("ClassCanBeRecord")
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        String token = authentication.getPrincipal().toString();

        if (token == null || !jwtProvider.validationToken(token)) {
            throw new BadCredentialsException("Validation failed");
        }

        Claims claims = jwtProvider.parseClaims(token);
        if (claims.get("roles") == null) {
            throw new BadCredentialsException("Token must contain claim 'roles'. token: " + token);
        }

        final long memberId;
        try {
            memberId = Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Failed to get subject from token. token: " + token, e);
        }

        final Member member;
        try {
            member = memberService.findById(memberId);
        } catch (UnauthorizedAccessRequestException e) {
            throw new AccountExpiredException("Member not found. memberId: " + memberId, e);
        }

        return new PreAuthenticatedAuthenticationToken(
                member.getId(),
                null,
                member.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
