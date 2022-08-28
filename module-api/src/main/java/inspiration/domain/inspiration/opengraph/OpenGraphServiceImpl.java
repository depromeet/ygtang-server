package inspiration.domain.inspiration.opengraph;

import com.github.siyoon210.ogparser4j.OgParser;
import com.github.siyoon210.ogparser4j.OpenGraph;
import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.inspiration.response.OpenGraphResponseVo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OpenGraphServiceImpl implements OpenGraphService {
    public OpenGraphResponseVo getOpenGraphResponseVo(InspirationType inspirationType, String link) {
        if (inspirationType != InspirationType.LINK) {
            return OpenGraphResponseVo.from(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        Optional<OpenGraphVo> openGraphVoOptional = getMetadata(link);
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

    private Optional<OpenGraphVo> getMetadata(String url) {
        // FIXME: DI 적용 (모듈 의존성 개선 후 api 모듈에서 bean 생성해야함)
        OgParser ogParser = new OgParser(new YgtangOgMetaElementHtmlParser());
        OpenGraph openGraph = ogParser.getOpenGraphOf(url);
        if (openGraph.getAllProperties().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
                OpenGraphVo.builder()
                           .image(getValueSafely(openGraph, "image"))
                           .siteName(getValueSafely(openGraph, "content"))
                           .title(getValueSafely(openGraph, "title"))
                           .url(getValueSafely(openGraph, "url"))
                           .description(getValueSafely(openGraph, "description"))
                           .build()
        );
    }

    private String getValueSafely(OpenGraph openGraph, String property) {
        final OpenGraph.Content content;
        try {
            content = openGraph.getContentOf(property);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
        return content.getValue();
    }
}
