package inspiration.v1.inspiration.opengraph;

import lombok.Data;

@Data
@SuppressWarnings("ClassCanBeRecord")
public class OpenGraphResponse {
    private final int code;
    private final String description;
    private final String siteName;
    private final String title;
    private final String url;
    private final String image;
}
