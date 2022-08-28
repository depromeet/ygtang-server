package inspiration.v1.inspiration.opengraph;

import inspiration.domain.inspiration.response.OpenGraphResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenGraphAssembler {

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
}
