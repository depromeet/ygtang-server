package inspiration.v1.member;

import inspiration.auth.AuthService;
import inspiration.auth.request.LoginRequest;
import inspiration.emailauth.request.ConfirmEmailRequest;
import inspiration.emailauth.request.EmailAuthRequest;
import inspiration.emailauth.EmailAuthService;
import inspiration.member.MemberService;
import inspiration.member.request.SignUpRequest;
import inspiration.member.response.NicknameResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final EmailAuthService emailAuthService;
    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping("/confirm-email")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "이메일 인증 링크 요청", notes = "이메일에 인증 링크를 요청한다")
    public void confirmEmail(@RequestBody @Valid ConfirmEmailRequest request) {
        emailAuthService.confirmEmail(request);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입", notes = "회원가입을 합니다.")
    public Long singUp(@RequestBody SignUpRequest request) {
        return memberService.signUp(request);
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "이메일로 로그인을 합니다.")
    public void login(@RequestBody LoginRequest request, HttpServletResponse httpServletResponse) {
        authService.login(request, httpServletResponse);
    }

    @GetMapping("/nicknames/{nickname}/exists")
    @ApiOperation(value = "닉네임 중복 확인", notes = "중복된 닉네임이 있는지 검사합니다.")
    public NicknameResponse confirmNickname(@PathVariable String nickname) {

        return memberService.confirmNickName(nickname);
    }

    @GetMapping("/auth-email")
    @ApiOperation(value = "이메일 인증.", notes = "링크를 클릭하면 이메일 인증에 성공한다.")
    public RedirectView emailAuth(@ModelAttribute EmailAuthRequest request) {
        return emailAuthService.emailAuth(request);
    }
}
