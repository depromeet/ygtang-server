package inspiration.v1.reissue;

import inspiration.ResultResponse;
import inspiration.auth.AuthService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reissue")
public class ReissueController {

    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "리프레쉬 토큰 재발급", notes = "리프레쉬 토큰을 재발급합니다.")
    public ResultResponse reissue(@RequestHeader(value = "REFRESH-TOKEN") String refreshToken) {

        return authService.reissue(refreshToken);
    }
}
