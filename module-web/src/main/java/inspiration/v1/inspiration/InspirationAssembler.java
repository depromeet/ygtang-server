package inspiration.v1.inspiration;

import inspiration.v1.RestPage;
import inspiration.aws.AwsS3Service;
import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.inspiration.request.InspirationAddRequestVo;
import inspiration.domain.inspiration.request.InspirationModifyRequestVo;
import inspiration.domain.inspiration.request.InspirationTagRequestVo;
import inspiration.domain.inspiration.response.InspirationResponseVo;
import inspiration.v1.inspiration.opengraph.OpenGraphAssembler;
import inspiration.v1.member.MemberAssembler;
import inspiration.v1.tag.TagAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class InspirationAssembler {
    private final MemberAssembler memberAssembler;
    private final TagAssembler tagAssembler;
    private final OpenGraphAssembler openGraphAssembler;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final AwsS3Service awsS3Service;

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
                inspirationResponseVo.getTagResponseVoList().stream()
                                     .map(tagAssembler::toTagResponse)
                                     .collect(Collectors.toList()),
                inspirationResponseVo.getType(),
                inspirationResponseVo.getContent(),
                inspirationResponseVo.getMemo(),
                openGraphAssembler.toOpenGraphResponse(inspirationResponseVo.getOpenGraphResponseVo()),
                inspirationResponseVo.getCreatedDateTime(),
                inspirationResponseVo.getUpdatedDateTime()
        );
    }

    public RestPage<InspirationResponse> toInspirationResponseRestPage(Page<Inspiration> inspirationPage) {
        return new RestPage<>(
                inspirationPage.stream()
                               .parallel()
                               .map(it -> (Callable<InspirationResponseVo>) () -> InspirationResponseVo.of(it, openGraphAssembler.getOpenGraphResponseVo(it.getType(), it.getContent()), awsS3Service))
                               .map(it -> threadPoolTaskExecutor.submitListenable(it).completable())
                               .map(CompletableFuture::join)
                               .map(this::toInspirationResponse)
                               .collect(Collectors.toList()),
                inspirationPage.getPageable().getPageNumber(),
                inspirationPage.getPageable().getPageSize(),
                inspirationPage.getTotalElements()
        );
    }

    public InspirationTagRequestVo toInspirationTagRequestVo(InspirationTagRequest request) {
        return new InspirationTagRequestVo(
                request.getId(),
                request.getTagId()
        );
    }
}
