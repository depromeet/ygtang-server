package inspiration.v1.signup;

import inspiration.v1.ResultResponse;
import inspiration.domain.member.request.ExtraInfoRequestVo;
import inspiration.enumeration.ExceptionType;
import inspiration.v1.auth.TokenResponse;
import inspiration.auth.TokenResponseVo;
import inspiration.v1.member.ExtraInfoRequest;
import inspiration.v1.member.SignUpRequest;
import inspiration.domain.member.request.SignUpRequestVo;
import inspiration.signup.SignupService;
import inspiration.v1.auth.AuthAssembler;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup")
@SuppressWarnings("ClassCanBeRecord")
public class SignUpController {
    private final SignupService signupService;
    private final SignUpAssembler signUpAssembler;
    private final AuthAssembler authAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입", notes = "회원가입을 합니다.")
    public ResultResponse<TokenResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        SignUpRequestVo signUpRequestVo = signUpAssembler.toSignUpRequestVo(request);
        TokenResponseVo tokenResponseVo = signupService.signUp(signUpRequestVo);
        TokenResponse tokenResponse = authAssembler.toTokenResponse(tokenResponseVo);
        return ResultResponse.success(tokenResponse);
    }

    @PatchMapping("/extra-informations")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "사용자 추가 정보", notes = "사용자 추가 정보를 저장합니다.")
    public void updateExtraInfo(
            @RequestBody @Valid ExtraInfoRequest request,
            @RequestParam(value = "email") String email
    ) {
        ExtraInfoRequestVo extraInfoRequestVo = signUpAssembler.toExtraInfoRequestVo(request);
        signupService.updateExtraInfo(email, extraInfoRequestVo);
    }

    @GetMapping("/nicknames/{nickname}/exists")
    @ApiOperation(value = "닉네임 중복 확인", notes = "중복된 닉네임이 있는지 검사합니다.")
    public ResultResponse<?> checkNickname(@PathVariable String nickname) {
        signupService.checkNickName(nickname);
        return ResultResponse.from("사용할 수 있는 닉네임입니다.");
    }

    @GetMapping("/{email}/status")
    @ApiOperation(value = "해당 유저가 가입되어 있는 유저인지 상태 여부 반환", notes = "해당 유저가 가입되어 있는 유저인지 상태 여부를 반환한다.")
    public ResultResponse<Boolean> validSignUpEmailStatus(@PathVariable String email) {
        boolean exist = signupService.existsMemberByEmail(email);
        return ResultResponse.of(
                exist ? ExceptionType.EMAIL_ALREADY_AUTHENTICATED : ExceptionType.EMAIL_NOT_AUTHENTICATED,
                exist
        );
    }
}
