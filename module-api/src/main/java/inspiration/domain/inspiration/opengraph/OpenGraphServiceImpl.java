package inspiration.domain.inspiration.opengraph;

import com.github.siyoon210.ogparser4j.OgParser;
import com.github.siyoon210.ogparser4j.OpenGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpenGraphServiceImpl implements OpenGraphService {
    private final YgtangOgMetaElementHtmlParser ygtangOgMetaElementHtmlParser;

    public Optional<OpenGraphVo> getMetadata(String url) {
        OgParser ogParser = new OgParser(ygtangOgMetaElementHtmlParser);
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
