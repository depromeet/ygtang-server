package inspiration.v1.inspiration.opengraph;

import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.inspiration.opengraph.OpenGraphService;
import inspiration.domain.inspiration.opengraph.OpenGraphVo;
import inspiration.domain.inspiration.response.OpenGraphResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class OpenGraphAssembler {
    private final OpenGraphService openGraphService;

    public OpenGraphResponse toOpenGraphResponse(OpenGraphResponseVo openGraphResponseVo) {
        return new OpenGraphResponse(
                openGraphResponseVo.getCode(),
                openGraphResponseVo.getDescription(),
                openGraphResponseVo.getSiteName(),
                openGraphResponseVo.getTitle(),
                openGraphResponseVo.getUrl(),
                openGraphResponseVo.getImage()
        );
    }

    public OpenGraphResponseVo getOpenGraphResponseVo(InspirationType inspirationType, String link) {
        if (inspirationType != InspirationType.LINK) {
            return OpenGraphResponseVo.from(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        Optional<OpenGraphVo> openGraphVoOptional = openGraphService.getMetadata(link);
        if (openGraphVoOptional.isEmpty()) {
            return OpenGraphResponseVo.from(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        OpenGraphVo openGraphVo = openGraphVoOptional.get();
        return OpenGraphResponseVo.of(
                HttpStatus.OK.value(),
                openGraphVo.getImage(),
                openGraphVo.getSiteName(),
                openGraphVo.getTitle(),
                openGraphVo.getUrl() != null ? openGraphVo.getUrl() : link,
                openGraphVo.getDescription()
        );
    }
}
