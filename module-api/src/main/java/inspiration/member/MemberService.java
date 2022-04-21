package inspiration.member;

import inspiration.exception.PostNotFoundException;
import inspiration.jwt.TokenProvider;
import inspiration.member.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final EmailAuthRepository emailAuthRepository;
    private final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        Member member = emailAuthRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new PostNotFoundException());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResponse jwt = tokenProvider.createToken(member.getEmail(), authentication);

        // redis에 refreshToken 저장

        return jwt;
    }
}
