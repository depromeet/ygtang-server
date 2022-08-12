package inspiration.v1.auth;

import inspiration.ResultResponse;
import inspiration.auth.AuthService;
import inspiration.auth.TokenResponse;
import inspiration.auth.request.LoginRequest;
import inspiration.domain.emailauth.EmailAuthService;
import inspiration.domain.emailauth.request.AuthenticateEmailRequest;
import inspiration.domain.emailauth.request.SendEmailRequest;
import inspiration.v1.TokenHelper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final EmailAuthService emailAuthService;
    private final AuthService authService;
    private final TokenHelper tokenHelper;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "로그인", notes = "이메일로 로그인을 합니다.")
    public ResultResponse<TokenResponse> login(
            @RequestBody LoginRequest request
    ) {
        ResultResponse<TokenResponse> loginResult = authService.login(request);
        tokenHelper.addHeaderForAccessToken(loginResult);
        tokenHelper.addCookieForRefreshToken(loginResult);
        return loginResult;
    }

    @PostMapping("/sends-email/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입을 위해 이메일 인증 링크 요청", notes = "회원가입을 위해 이메일에 인증 링크를 요청한다")
    public void signUpEmailSend(@RequestBody @Valid SendEmailRequest request) {

        emailAuthService.signUpEmailSend(request.getEmail());
    }

    @PostMapping("/sends-email/passwords/reset")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "비밀번호 초기화를 위해 이메일 인증 링크 요청", notes = "비밀번호 초기화를 위해 이메일 인증 링크를 요청한다.")
    public void resetPasswordForAuthEmailSend(@RequestBody @Valid SendEmailRequest request) {

        emailAuthService.resetPasswordForAuthEmailSend(request.getEmail());
    }

    @GetMapping("/email/signup")
    @ApiOperation(value = "회원가입을 위한 이메일 인증.", notes = "링크를 클릭하면 회원가입을 위한 이메일 인증에 성공한다.")
    public RedirectView authenticateEmailOfSingUp(@ModelAttribute AuthenticateEmailRequest request) {

        return emailAuthService.authenticateEmailOfSingUp(request.getEmail());
    }

    @GetMapping("/email/passwords/reset")
    @ApiOperation(value = "비밀번호 초기화를 위한 이메일 인증", notes = "링크를 클릭하면 비밀번호 초기화를 위한 이메일 인증에 성공한다.")
    public RedirectView authenticateEmailOfResetPasswordForAuth(@ModelAttribute AuthenticateEmailRequest request) {

        return emailAuthService.authenticateEmailOfResetPasswordForAuth(request.getEmail());
    }

    @GetMapping("/signup/email/{email}/status")
    @ApiOperation(value = "회원가입을 위한 해당 이메일의 인증 상태 여부 반환", notes = "회원가입을 위한 해당 이메일의 인증 상태 여부를 반환한다.")
    public ResultResponse validAuthenticateEmailStatusOfSignup(@PathVariable String email) {

        return emailAuthService.validAuthenticateEmailStatusOfSignup(email);
    }

    @GetMapping("/passwords/reset/email/{email}/status")
    @ApiOperation(value = "비밀번호 초기화를 위한 이메일 인증 상태 여부 반환", notes = "비밀번호 초기화를 위한 해당 이메일의 인증 상태 여부를 반환한다.")
    public ResultResponse validAuthenticateEmailStatusOfResetPassword(@PathVariable String email) {

        return emailAuthService.validAuthenticateEmailStatusOfResetPassword(email);
    }
}
