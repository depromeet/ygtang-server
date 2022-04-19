package inspiration.v1.member;

import inspiration.member.request.LoginRequest;
import inspiration.member.MemberService;
import inspiration.member.request.TokenRequest;
import inspiration.member.loginAuth.LoginAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<TokenRequest> login(@RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {
        TokenRequest token = memberService.login(loginRequest);

        Cookie accessToken = new Cookie("accessToken", token.getAccessToken());
        accessToken.setSecure(true);
        accessToken.setHttpOnly(true);
        accessToken.setPath("/");

        Cookie refreshToken = new Cookie("refreshToken", token.getRefreshToken());
        refreshToken.setSecure(true);
        refreshToken.setHttpOnly(true);
        refreshToken.setPath("/");

        response.addCookie(accessToken);
        response.addCookie(refreshToken);

        return ResponseEntity.ok(token);
    }

}
