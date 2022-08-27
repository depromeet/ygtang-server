package inspiration.domain.inspiration.response;

import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class OpenGraphResponseVo {
    int code;
    String description;
    String siteName;
    String title;
    String url;
    String image;

    public static OpenGraphResponseVo of(
            int code,
            String image,
            String siteName,
            String title,
            String url,
            String description
    ) {
        return new OpenGraphResponseVo(
                code,
                image,
                siteName,
                title,
                url,
                description
        );
    }

    public static OpenGraphResponseVo from(int code) {
        return new OpenGraphResponseVo(
                code,
                null,
                null,
                null,
                null,
                null
        );
    }
}
