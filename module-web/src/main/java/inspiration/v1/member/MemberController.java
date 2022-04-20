package inspiration.v1.member;

import inspiration.emailauth.request.ConfirmEmailRequest;
import inspiration.emailauth.request.EmailAuthRequest;
import inspiration.emailauth.EmailAuthService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final EmailAuthService emailAuthService;

    @PostMapping("/confirm-email")
    @ApiOperation(value = "이메일 인증 링크 요청", notes = "이메일에 인증 링크를 요청한다")
    public void confirmEmail(@RequestBody @Valid ConfirmEmailRequest request) {
        emailAuthService.confirmEmail(request);
    }

    @GetMapping("/auth-email")
    @ApiOperation(value = "이메일 인증.", notes = "링크를 클릭하면 이메일 인증에 성공한다.")
    public RedirectView emailAuth(@ModelAttribute EmailAuthRequest request) {
        return emailAuthService.emailAuth(request);
    }
}
