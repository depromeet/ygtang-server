package inspiration.v1.inspiration;

import inspiration.domain.inspiration.opengraph.OpenGraphService;
import inspiration.domain.inspiration.request.InspirationAddRequestVo;
import inspiration.domain.inspiration.request.InspirationModifyRequestVo;
import inspiration.domain.inspiration.request.InspirationTagRequestVo;
import inspiration.domain.inspiration.response.InspirationResponseVo;
import inspiration.v1.RestPage;
import inspiration.v1.inspiration.opengraph.OpenGraphAssembler;
import inspiration.v1.member.MemberAssembler;
import inspiration.v1.tag.TagAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class InspirationAssembler {
    private final MemberAssembler memberAssembler;
    private final TagAssembler tagAssembler;
    private final OpenGraphAssembler openGraphAssembler;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final OpenGraphService openGraphService;

    public InspirationAddRequestVo toInspirationAddRequestVo(InspirationAddRequest inspirationAddRequest) {
        return new InspirationAddRequestVo(
                inspirationAddRequest.getType(),
                inspirationAddRequest.getContent(),
                inspirationAddRequest.getMemo(),
                inspirationAddRequest.getFile(),
                inspirationAddRequest.getTagIds()
        );
    }

    public InspirationModifyRequestVo toInspirationModifyRequestVo(InspirationModifyRequest inspirationModifyRequest) {
        return new InspirationModifyRequestVo(
                inspirationModifyRequest.getId(),
                inspirationModifyRequest.getMemo()
        );
    }

    public InspirationResponse toInspirationResponse(InspirationResponseVo inspirationResponseVo) {
        return new InspirationResponse(
                inspirationResponseVo.getId(),
                memberAssembler.toMemberResponse(inspirationResponseVo.getMemberResponseVo()),
                inspirationResponseVo.getTagResponseVoList()
                                     .stream()
                                     .map(tagAssembler::toTagResponse)
                                     .toList(),
                inspirationResponseVo.getType(),
                inspirationResponseVo.getContent(),
                inspirationResponseVo.getMemo(),
                openGraphAssembler.toOpenGraphResponse(
                        openGraphService.getOpenGraphResponseVo(
                                inspirationResponseVo.getType(),
                                inspirationResponseVo.getContent()
                        )
                ),
                inspirationResponseVo.getCreatedDateTime(),
                inspirationResponseVo.getUpdatedDateTime()
        );
    }

    public RestPage<InspirationResponse> toInspirationResponseRestPage(Page<InspirationResponseVo> inspirationResponseVoPage) {
        return new RestPage<>(
                inspirationResponseVoPage.stream()
                               .parallel()
                               .map(it -> (Callable<InspirationResponse>) () -> toInspirationResponse(it))
                               .map(it -> threadPoolTaskExecutor.submitListenable(it).completable())
                               .map(CompletableFuture::join)
                               .toList(),
                inspirationResponseVoPage.getPageable().getPageNumber(),
                inspirationResponseVoPage.getPageable().getPageSize(),
                inspirationResponseVoPage.getTotalElements()
        );
    }

    public InspirationTagRequestVo toInspirationTagRequestVo(InspirationTagRequest request) {
        return new InspirationTagRequestVo(
                request.getId(),
                request.getTagId()
        );
    }
}
