package inspiration.v1.reissue;

import inspiration.auth.AuthService;
import inspiration.auth.TokenResponseVo;
import inspiration.v1.ResultResponse;
import inspiration.v1.auth.AuthAssembler;
import inspiration.v1.auth.TokenResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reissue")
@SuppressWarnings("ClassCanBeRecord")
public class ReissueController {

    private final AuthService authService;
    private final AuthAssembler authAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "리프레쉬 토큰 재발급", notes = "리프레쉬 토큰을 재발급합니다.")
    public ResultResponse<TokenResponse> reissue(@RequestHeader(value = "REFRESH-TOKEN") String refreshToken) {
        TokenResponseVo tokenResponseVo = authService.reissue(refreshToken);
        TokenResponse tokenResponse = authAssembler.toTokenResponse(tokenResponseVo);
        return ResultResponse.success(tokenResponse);
    }
}
