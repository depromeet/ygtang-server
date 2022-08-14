package inspiration.v1.signup;

import inspiration.ResultResponse;
import inspiration.auth.TokenResponse;
import inspiration.domain.member.request.ExtraInfoRequest;
import inspiration.domain.member.request.SignUpRequest;
import inspiration.signup.SignupService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup")
public class SignUpController {
    private final SignupService signupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입", notes = "회원가입을 합니다.")
    public ResultResponse<TokenResponse> signUp(@RequestBody @Valid SignUpRequest request) {

        ResultResponse<TokenResponse> signUpResult = signupService.signUp(request);
        return signUpResult;
    }

    @PatchMapping("/extra-informations")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "사용자 추가 정보", notes = "사용자 추가 정보를 저장합니다.")
    public void updateExtraInfo(@RequestBody @Valid ExtraInfoRequest request,
                                @RequestParam(value = "email") String email) {

        signupService.updateExtraInfo(email, request);
    }

    @GetMapping("/nicknames/{nickname}/exists")
    @ApiOperation(value = "닉네임 중복 확인", notes = "중복된 닉네임이 있는지 검사합니다.")
    public ResultResponse checkNickname(@PathVariable String nickname) {

        return signupService.checkNickName(nickname);
    }

    @GetMapping("/{email}/status")
    @ApiOperation(value = "해당 유저가 가입되어 있는 유저인지 상태 여부 반환", notes = "해당 유저가 가입되어 있는 유저인지 상태 여부를 반환한다.")
    public ResultResponse validSignUpEmailStatus(@PathVariable String email) {

        return signupService.validSignUpEmailStatus(email);
    }
}
