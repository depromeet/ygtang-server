package inspiration.v1.inspiration;

import inspiration.config.AuthenticationPrincipal;
import inspiration.inspiration.InspirationService;
import inspiration.inspiration.request.InspirationAddRequest;
import inspiration.inspiration.request.InspirationModifyRequest;
import inspiration.inspiration.request.InspirationTagRequest;
import inspiration.inspiration.response.InspirationResponse;
import inspiration.v1.ResultResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inspiration")
public class InspirationController {

    private final InspirationService inspirationService;

    @GetMapping("/list")
    @ApiOperation(value = "영감 조회", notes = "영감 리스트를 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다.")
            , @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse> inspirationList(Pageable pageable, @ApiIgnore @AuthenticationPrincipal Long memberId ) {
        Page<InspirationResponse> inspirationResponsePage = inspirationService.findInspirations(pageable, memberId);
        return ResponseEntity.ok().body(ResultResponse.from(inspirationResponsePage));
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "영감 등록", notes = "영감 등록을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다.")
            , @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse> inspirationAdd(HttpServletRequest httpServletRequest,  @RequestPart(required = false) List<MultipartFile> files, @ModelAttribute @Valid InspirationAddRequest request, @ApiIgnore @AuthenticationPrincipal Long memberId ) {
        Long id = inspirationService.addInspiration(request, files, memberId);

        final URI uri = URI.create(httpServletRequest.getRequestURI() + "/" + id);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/modify")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "영감 수정(메모 수정)", notes = "영감 수정을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다.")
            ,@ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다.")
            , @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<ResultResponse> inspirationModify(HttpServletRequest httpServletRequest, @RequestBody  InspirationModifyRequest request, @ApiIgnore @AuthenticationPrincipal Long memberId) {
        Long id = inspirationService.modifyMemo(request, memberId);

        final URI uri = URI.create(httpServletRequest.getRequestURI() + "/" + id);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/tag")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "영감 태깅", notes = "영감 태깅을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다.")
            ,@ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다. | 존재하지 않는 태그ID 입니다.")
            , @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<ResultResponse> inspirationTagging(HttpServletRequest httpServletRequest, @RequestBody InspirationTagRequest request, @ApiIgnore @AuthenticationPrincipal Long memberId) {
        Long id = inspirationService.tagInspiration(request, memberId);

        final URI uri = URI.create(httpServletRequest.getRequestURI() + "/" + id);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation(value = "영감 삭제", notes = "영감 삭제를 요청한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 삭제되었습니다.")
            ,@ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다.")
            , @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<ResultResponse> inspirationRemove(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal Long memberId) {
        inspirationService.removeInspiration(id, memberId);
        return ResponseEntity.ok().build();
    }
}
