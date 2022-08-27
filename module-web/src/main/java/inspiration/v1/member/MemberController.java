package inspiration.v1.member;

import inspiration.auth.AuthService;
import inspiration.domain.inspiration.InspirationService;
import inspiration.domain.member.MemberService;
import inspiration.domain.member.response.MemberInfoVo;
import inspiration.domain.tag.TagService;
import inspiration.infrastructure.security.AuthenticationPrincipal;
import inspiration.signup.SignupService;
import inspiration.v1.auth.SendEmailRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@SuppressWarnings("ClassCanBeRecord")
public class MemberController {
    private final MemberService memberService;
    private final SignupService signupService;
    private final InspirationService inspirationService;
    private final TagService tagService;
    private final MemberAssembler memberAssembler;

    @PutMapping("/passwords/change")
    @ApiOperation(value = "패스워드를 변경.", notes = "패스워드를 변경한다.")
    public void changePassword(
            @ApiIgnore @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        memberService.changePassword(memberId, request.getConfirmPassword(), request.getPassword());
    }

    @PutMapping("/nickname/change")
    @ApiOperation(value = "닉네임 변경", notes = "닉네임을 변경한다.")
    public void changeNickname(
            @ApiIgnore @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid UpdateNicknameRequest request
    ) {
        signupService.checkNickName(request.getNickname());
        memberService.changeNickname(memberId, request.getNickname());
    }

    @PostMapping("/sends-email/reset-passwords")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "초기화된 비밀번호를 이메일로 전송", notes = "초기화된 비밀번호를 이메일로 전송한다.")
    public void resetPasswordEmailSend(@RequestBody @Valid SendEmailRequest request) {
        memberService.resetPasswordEmailSend(request.getEmail());
    }

    @GetMapping("/info")
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보를 조회한다.")
    public MemberInfoResponse getMemberInfo(@ApiIgnore @AuthenticationPrincipal Long memberId) {
        MemberInfoVo memberInfoVo = memberService.getMemberInfo(memberId);
        return memberAssembler.toMemberInfoResponse(memberInfoVo);
    }

    @DeleteMapping("/remove")
    @ApiOperation(value = "계정 삭제", notes = "계정을 삭제한다.")
    public void removeMember(@ApiIgnore @AuthenticationPrincipal Long memberId) {
        inspirationService.removeAllInspiration(memberId);
        tagService.removeAllTag(memberId);
        memberService.removeUser(memberId);
    }
}
