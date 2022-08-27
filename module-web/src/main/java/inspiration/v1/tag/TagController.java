package inspiration.v1.tag;

import inspiration.v1.RestPage;
import inspiration.domain.inspiration.InspirationService;
import inspiration.domain.tag.TagService;
import inspiration.domain.tag.request.TagAddRequestVo;
import inspiration.domain.tag.response.TagResponseVo;
import inspiration.infrastructure.AuthenticationPrincipal;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tag")
@SuppressWarnings("ClassCanBeRecord")
public class TagController {

    private final TagService tagService;
    private final InspirationService inspirationService;
    private final TagAssembler tagAssembler;

    @GetMapping("/list")
    @ApiOperation(value = "태그 조회", notes = "태그 리스트를 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse<RestPage<TagResponse>>> tagList(
            Pageable pageable,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        Page<TagResponse> tagResponsePage = tagService.findTags(pageable, memberId)
                                                      .map(tagAssembler::toTagResponse);
        RestPage<TagResponse> tagResponseRestPage = new RestPage<>(tagResponsePage);
        return ResponseEntity.ok().body(ResultResponse.success(tagResponseRestPage));
    }

    @GetMapping("/index/{keyword}")
    @ApiOperation(value = "태그 검색(LIKE)", notes = "태그를 키워드로 검색한다.(LIKE)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse<RestPage<TagResponse>>> indexTag(
            Pageable pageable,
            @PathVariable @NotBlank String keyword,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        Page<TagResponse> tagResponsePage = tagService.indexTags(pageable, keyword, memberId)
                                                      .map(tagAssembler::toTagResponse);
        RestPage<TagResponse> tagResponseRestPage = new RestPage<>(tagResponsePage);
        return ResponseEntity.ok().body(ResultResponse.success(tagResponseRestPage));
    }

    @GetMapping("/search/{keyword}")
    @ApiOperation(value = "태그 검색(일치)", notes = "태그를 키워드로 검색한다.(일치)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<ResultResponse<RestPage<TagResponse>>> searchTag(
            Pageable pageable,
            @PathVariable @NotBlank String keyword,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        Page<TagResponse> tagResponsePage = tagService.searchTags(pageable, keyword, memberId)
                                                      .map(tagAssembler::toTagResponse);
        RestPage<TagResponse> tagResponseRestPage = new RestPage<>(tagResponsePage);
        return ResponseEntity.ok().body(ResultResponse.success(tagResponseRestPage));
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "태그 등록", notes = "태그 등록을 요청한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 등록되었습니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다.")
    })
    public ResponseEntity<TagResponse> tagAdd(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid TagAddRequest request,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        TagAddRequestVo tagAddRequestVo = tagAssembler.toTagAddRequestVo(request);
        TagResponseVo tagResponseVo = tagService.addTag(tagAddRequestVo, memberId);
        TagResponse tagResponse = tagAssembler.toTagResponse(tagResponseVo);
        final URI uri = URI.create(httpServletRequest.getRequestURI() + "/" + tagResponse.getId());
        return ResponseEntity.created(uri).body(tagResponse);
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation(value = "태그 삭제", notes = "태그 삭제를 요청한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 삭제되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 태그ID 입니다."),
            @ApiResponse(code = 401, message = "토큰이 정상적으로 인증되지 않았습니다. | 해당 리소스 수정권한이 없습니다.")
    })
    public ResponseEntity<?> tagRemove(
            @PathVariable @NotNull Long id,
            @ApiIgnore @AuthenticationPrincipal Long memberId
    ) {
        inspirationService.unTagInspirationByTag(id, memberId);
        tagService.removeTag(id, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/all")
    @ApiOperation(value = "태그 전체 삭제", notes = "태그를 전체삭제한다.")
    public void tagRemoveAll(@ApiIgnore @AuthenticationPrincipal Long memberId) {
        tagService.removeAllTag(memberId);
    }
}
