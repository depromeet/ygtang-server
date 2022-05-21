package inspiration.v1.member;

import inspiration.config.AuthenticationPrincipal;
import inspiration.emailauth.request.SendEmailRequest;
import inspiration.member.MemberService;
import inspiration.member.request.UpdatePasswordRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/passwords/change")
    @ApiOperation(value = "패스워드를 변경.", notes = "패스워드를 변경한다.")
    public void changePassword(@ApiIgnore @AuthenticationPrincipal Long memberId, @RequestBody @Valid UpdatePasswordRequest request) {

        memberService.changePassword(memberId, request.getConfirmPassword(), request.getPassword());
    }

    @PostMapping("/sends-email/reset-passwords")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "초기화된 비밀번호를 이메일로 전송", notes = "초기화된 비밀번호를 이메일로 전송한다.")
    public void resetPasswordEmailSend(@RequestBody @Valid SendEmailRequest request) {

        memberService.resetPasswordEmailSend(request.getEmail());
    }
}
