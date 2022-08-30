package inspiration.v1.inspiration;

import inspiration.domain.inspiration.opengraph.OpenGraphService;
import inspiration.v1.RestPage;
import inspiration.domain.inspiration.InspirationService;
import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.inspiration.request.InspirationAddRequestVo;
import inspiration.domain.inspiration.request.InspirationModifyRequestVo;
import inspiration.domain.inspiration.request.InspirationTagRequestVo;
import inspiration.domain.inspiration.response.InspirationResponseVo;
import inspiration.domain.inspiration.response.OpenGraphResponseVo;
import inspiration.infrastructure.security.AuthenticationPrincipal;
import inspiration.v1.ResultResponse;
import inspiration.v1.inspiration.opengraph.OpenGraphAssembler;
import inspiration.v1.inspiration.opengraph.OpenGraphResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inspiration")
@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class InspirationController {
    private final InspirationService inspirationService;
    private final InspirationAssembler inspirationAssembler;
    private final OpenGraphService openGraphService;
    private final OpenGraphAssembler openGraphAssembler;

    @GetMapping("/list")
    @ApiOperation(value = "영감 조회", notes = "영감 리스트를 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse<Page<InspirationResponse>>> inspirationList(
            Pageable pageable,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        Page<InspirationResponseVo> inspirationResponseVoPage = inspirationService.findInspirations(pageable, memberId);
        RestPage<InspirationResponse> inspirationResponseRestPage =
                inspirationAssembler.toInspirationResponseRestPage(inspirationResponseVoPage);
        return ResponseEntity.ok().body(
                ResultResponse.success(inspirationResponseRestPage)
        );
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "영감 조회", notes = "영감 id로 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse<InspirationResponse>> findInspiration(
            @PathVariable @NotNull Long id,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        InspirationResponseVo inspirationResponseVo = inspirationService.findInspiration(id, memberId);
        InspirationResponse inspirationResponse = inspirationAssembler.toInspirationResponse(inspirationResponseVo);
        return ResponseEntity.ok().body(ResultResponse.success(inspirationResponse));
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "영감 등록", notes = "영감 등록을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<Object> inspirationAdd(
            @ModelAttribute @Valid InspirationAddRequest request,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        InspirationAddRequestVo inspirationAddRequestVo = inspirationAssembler.toInspirationAddRequestVo(request);
        Long id = inspirationService.addInspiration(inspirationAddRequestVo, memberId);
        final URI uri = UriComponentsBuilder.newInstance()
                                            .path("/api/v1/inspiration/{inspirationId}")
                                            .build(id);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/modify")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "영감 수정(메모 수정)", notes = "영감 수정을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<Object> inspirationModify(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid InspirationModifyRequest request,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        InspirationModifyRequestVo inspirationModifyRequestVo = inspirationAssembler.toInspirationModifyRequestVo(request);
        Long id = inspirationService.modifyMemo(inspirationModifyRequestVo, memberId);
        final URI uri = URI.create(httpServletRequest.getRequestURI() + "/" + id);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/tag")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "영감 태깅", notes = "영감 태깅을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다. | 존재하지 않는 태그ID 입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<Object> inspirationTagging(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid InspirationTagRequest request,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        InspirationTagRequestVo inspirationTagRequestVo = inspirationAssembler.toInspirationTagRequestVo(request);
        Long id = inspirationService.tagInspiration(inspirationTagRequestVo, memberId);
        final URI uri = URI.create(httpServletRequest.getRequestURI() + "/" + id);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/tag/")
    @ApiOperation(value = "영감 필터링", notes = "조건(태그, 영감 타입, 영감 생성 일자)에 맞는 영감 조회를 요청한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<ResultResponse<RestPage<InspirationResponse>>> findInspirationByTag(
            Pageable pageable,
            @RequestBody List<Long> tagIds,
            @RequestParam(required = false) List<InspirationType> types,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdDateTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdDateTimeTo,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        Page<InspirationResponseVo> inspirationResponseVoPage =
                inspirationService.findInspirationsByTags(tagIds, types, createdDateTimeFrom, createdDateTimeTo, memberId, pageable);
        RestPage<InspirationResponse> inspirationResponseRestPage =
                inspirationAssembler.toInspirationResponseRestPage(inspirationResponseVoPage);
        return ResponseEntity.ok().body(
                ResultResponse.success(inspirationResponseRestPage)
        );
    }

    @DeleteMapping("/untag/{id}/{tagId}")
    @ApiOperation(value = "영감 태깅 해제", notes = "영감 태깅을 해제한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 해제되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다. | 존재하지 않는 태그ID 입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<Object> inspirationUntagging(
            @PathVariable @NotNull Long id,
            @PathVariable @NotNull Long tagId,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        inspirationService.unTagInspiration(id, tagId, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation(value = "영감 삭제", notes = "영감 삭제를 요청한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 삭제되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 영감ID 입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<Object> inspirationRemove(
            @PathVariable @NotNull Long id,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        inspirationService.unTagInspirationByInspiration(id, memberId);
        inspirationService.removeInspiration(id, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/all")
    @ApiOperation(value = "영감 전체 삭제", notes = "해당 사용자의 전체 영감을 삭제한다.")
    public void inspirationRemoveAll(@ApiIgnore @AuthenticationPrincipal Long memberId) {
        inspirationService.removeAllInspiration(memberId);
    }

    @GetMapping("/link/availiable")
    @ApiOperation(value = "링크 적합성 판단", notes = "링크 적합성을 판단한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 400, message = "적합하지 않은 링크입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse<OpenGraphResponse>> inspirationList(@RequestParam @NotBlank String link) {
        OpenGraphResponseVo openGraphResponseVo = openGraphService.getOpenGraphResponseVo(InspirationType.LINK, link);
        OpenGraphResponse openGraphResponse = openGraphAssembler.toOpenGraphResponse(openGraphResponseVo);
        return ResponseEntity.ok().body(ResultResponse.success(openGraphResponse));
    }
}
