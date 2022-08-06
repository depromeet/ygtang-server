package inspiration.inspiration.opengraph;

import com.github.siyoon210.ogparser4j.htmlparser.OgMetaElement;
import com.github.siyoon210.ogparser4j.htmlparser.OgMetaElementHtmlParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class YgtangOgMetaElementHtmlParser implements OgMetaElementHtmlParser {
    @Override
    public List<OgMetaElement> getOgMetaElementsFrom(String url) {
        try {
            final Document document = Jsoup.connect(url)
                                           .timeout(1000)
                                           .get();
            final Elements metaElements = document.select("meta");
            List<OgMetaElement> ogMetaElements = metaElements.stream()
                                                             .filter(m -> m.attr("property").startsWith("og:"))
                                                             .map(m -> {
                                                                 final String property = m.attr("property").substring(3).trim();
                                                                 final String content = m.attr("content");
                                                                 return new OgMetaElement(property, content);
                                                             })
                                                             .collect(Collectors.toList());
            addDescriptionIfNotExists(ogMetaElements, document);
            addTitleIfNotExists(ogMetaElements, document);
            return ogMetaElements;
        } catch (IOException | IndexOutOfBoundsException | IllegalArgumentException e) {
            log.warn("Failed to parse OpenGraph Metadata. url:{}", url, e);
            return Collections.emptyList();
        }
    }

    private void addDescriptionIfNotExists(List<OgMetaElement> ogMetaElements, Document document) {
        Optional<OgMetaElement> descriptionOptional = ogMetaElements.stream()
                                                                    .filter(it -> "description".equals(it.getProperty()))
                                                                    .findFirst();
        if (descriptionOptional.isEmpty() || !StringUtils.hasLength(descriptionOptional.get().getContent())) {
            Elements metaDescription = document.select("meta[name=description]");
            if (!metaDescription.isEmpty()) {
                ogMetaElements.add(
                        new OgMetaElement("description", metaDescription.get(0).attr("content"))
                );
            }
        }
    }

    private void addTitleIfNotExists(List<OgMetaElement> ogMetaElements, Document document) {
        Optional<OgMetaElement> titleOptional = ogMetaElements.stream()
                                                              .filter(it -> "title".equals(it.getProperty()))
                                                              .findFirst();
        if (titleOptional.isEmpty() || !StringUtils.hasLength(titleOptional.get().getContent())) {
            ogMetaElements.add(new OgMetaElement("title", document.title()));
        }
    }
}
