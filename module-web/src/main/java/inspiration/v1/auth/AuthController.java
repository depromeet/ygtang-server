package inspiration.v1.auth;

import inspiration.auth.AuthService;
import inspiration.auth.TokenResponseVo;
import inspiration.auth.request.LoginRequestVo;
import inspiration.domain.emailauth.EmailAuthService;
import inspiration.v1.ResultResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

import static inspiration.enumeration.ExceptionType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@SuppressWarnings("ClassCanBeRecord")
public class AuthController {

    private final EmailAuthService emailAuthService;
    private final AuthService authService;
    private final AuthAssembler authAssembler;

    @Value("${ygtang.redirect-url.password-auth}")
    private String passwordAuthRedirectUrl;
    @Value("${ygtang.redirect-url.policy}")
    private String policyRedirectUrl;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "로그인", notes = "이메일로 로그인을 합니다.")
    public ResultResponse<TokenResponse> login(
            @RequestBody LoginRequest request
    ) {
        LoginRequestVo loginRequestVo = authAssembler.toLoginRequestVo(request);
        TokenResponseVo tokenResponseVo = authService.login(loginRequestVo);
        TokenResponse tokenResponse = authAssembler.toTokenResponse(tokenResponseVo);
        return ResultResponse.success(tokenResponse);
    }

    @PostMapping("/sends-email/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입을 위해 이메일 인증 링크 요청", notes = "회원가입을 위해 이메일에 인증 링크를 요청한다")
    public void sendEmailForSignup(@RequestBody @Valid SendEmailRequest request) {

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

        emailAuthService.authenticateEmailOfSignUp(request.getEmail());
        return new RedirectView(policyRedirectUrl);
    }

    @GetMapping("/email/passwords/reset")
    @ApiOperation(value = "비밀번호 초기화를 위한 이메일 인증", notes = "링크를 클릭하면 비밀번호 초기화를 위한 이메일 인증에 성공한다.")
    public RedirectView authenticateEmailOfResetPasswordForAuth(@ModelAttribute AuthenticateEmailRequest request) {

        emailAuthService.authenticateEmailOfResetPasswordForAuth(request.getEmail());
        return new RedirectView(passwordAuthRedirectUrl + request.getEmail());
    }

    @GetMapping("/signup/email/{email}/status")
    @ApiOperation(value = "회원가입을 위한 해당 이메일의 인증 상태 여부 반환", notes = "회원가입을 위한 해당 이메일의 인증 상태 여부를 반환한다.")
    public ResultResponse<Boolean> validAuthenticateEmailStatusOfSignup(@PathVariable String email) {
        boolean isValid = emailAuthService.validAuthenticateEmailStatusOfSignup(email);
        return ResultResponse.of(
                isValid ? EMAIL_ALREADY_AUTHENTICATED : EMAIL_NOT_AUTHENTICATED,
                isValid
        );
    }

    @GetMapping("/passwords/reset/email/{email}/status")
    @ApiOperation(value = "비밀번호 초기화를 위한 이메일 인증 상태 여부 반환", notes = "비밀번호 초기화를 위한 해당 이메일의 인증 상태 여부를 반환한다.")
    public ResultResponse<Boolean> validAuthenticateEmailStatusOfResetPassword(@PathVariable String email) {
        boolean isValid = emailAuthService.isValidAuthenticateEmailStatusOfResetPassword(email);
        return ResultResponse.of(
                isValid ? EMAIL_ALREADY_AUTHENTICATED : EMAIL_NOT_AUTHENTICATED,
                isValid
        );
    }
}
