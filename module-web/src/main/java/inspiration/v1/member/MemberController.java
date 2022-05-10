package inspiration.v1.member;

import inspiration.config.AuthenticationPrincipal;
import inspiration.member.MemberService;
import inspiration.member.request.UpdatePasswordRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/passwords/reset")
    @ApiOperation(value = "패스워드를 변경.", notes = "패스워드를 변경한다.")
    public void updatePassword(@ApiIgnore @AuthenticationPrincipal Long memberId, @RequestBody @Valid UpdatePasswordRequest request) {

        memberService.updatePassword(memberId, request.getConfirmPassword(), request.getPassword());
    }
}
