package inspiration.v1.auth;

import inspiration.ResultResponse;
import inspiration.auth.AuthService;
import inspiration.auth.request.LoginRequest;
import inspiration.emailauth.EmailAuthService;
import inspiration.emailauth.request.AuthenticateEmailRequest;
import inspiration.emailauth.request.SendEmailRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final EmailAuthService emailAuthService;
    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "로그인", notes = "이메일로 로그인을 합니다.")
    public ResultResponse login(@RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/sends-email")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입을 위해 이메일 인증 링크 요청", notes = "회원가입을 위해 이메일에 인증 링크를 요청한다")
    public void signUpEmailSend(@RequestBody @Valid SendEmailRequest request) {

        emailAuthService.signUpEmailSend(request.getEmail());
    }

    @PostMapping("/password/sends-email/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "비빌번호 변경을 위해 이메일 인증 링크 요청", notes = "비밀번호 변경을 위해 이메일에 인증 링크를 요청한다")
    public void updatePasswordEmailSend(@PathVariable String email) {

        emailAuthService.updatePasswordEmailSend(email);
    }

    @GetMapping("/email")
    @ApiOperation(value = "회원가입을 위한 이메일 인증.", notes = "링크를 클릭하면 회원가입을 위한 이메일 인증에 성공한다.")
    public RedirectView authenticateEmailOfSingUp(@ModelAttribute AuthenticateEmailRequest request) {

        return emailAuthService.authenticateEmailOfSingUp(request.getEmail());
    }

    @GetMapping("/password/reset")
    @ApiOperation(value = "비밀번호 변경을 위한 이메일 인증.", notes = "링크를 클릭하면 비밀번호 변경을 위한 이메일 인증에 성공한다.")
    public RedirectView authenticateEmailOfPassword(@ModelAttribute AuthenticateEmailRequest request) {

        return emailAuthService.authenticateEmailOfUpdatePassword(request.getEmail());
    }

    @GetMapping("/email/{email}/status")
    @ApiOperation(value = "해당 이메일의 인증 상태 여부 반환", notes = "해당 이메일의 인증 상태 여부를 반환한다.")
    public ResultResponse validAuthenticateEmailStatus(@PathVariable String email) {

        return emailAuthService.validAuthenticateEmailStatus(email);
    }
}
