package inspiration.v1.member;

import inspiration.auth.AuthService;
import inspiration.config.AuthenticationPrincipal;
import inspiration.member.MemberService;
import inspiration.member.request.UpdatePasswordRequest;
import inspiration.member.response.MemberInfoResponse;
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
    private final AuthService authService;

    @PutMapping("/passwords/reset")
    @ApiOperation(value = "패스워드를 변경.", notes = "패스워드를 변경한다.")
    public void updatePassword(@ApiIgnore @AuthenticationPrincipal Long memberId, @RequestBody @Valid UpdatePasswordRequest request) {

        memberService.updatePassword(memberId, request.getConfirmPassword(), request.getPassword());
    }

    @PostMapping("/info")
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보를 조회한다.")
    public MemberInfoResponse getUserInfo() {

        return authService.getUserInfo();
    }
}
