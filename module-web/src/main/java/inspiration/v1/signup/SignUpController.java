package inspiration.v1.signup;

import inspiration.ResultResponse;
import inspiration.member.request.SignUpRequest;
import inspiration.signup.SignupService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup")
public class SignUpController {
    private final SignupService signupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "회원가입", notes = "회원가입을 합니다.")
    public Long singUp(@RequestBody SignUpRequest request) {

        return signupService.signUp(request);
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
