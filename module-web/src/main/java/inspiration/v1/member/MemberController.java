package inspiration.v1.member;

import inspiration.member.MemberService;
import inspiration.member.request.UpdatePasswordRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/{id}/passwords}")
    @ApiOperation(value = "패스워드를 변경.", notes = "패스워드를 변경한다.")
    public void updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest request) {

        memberService.updatePassword(id, request.getConfirmPassword(), request.getPassword());
    }
}
