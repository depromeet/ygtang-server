package inspiration.domain.inspiration.opengraph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@SuppressWarnings("ClassCanBeRecord")
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenGraphVo {
    String description;
    String siteName;
    String title;
    String url;
    String image;
}
