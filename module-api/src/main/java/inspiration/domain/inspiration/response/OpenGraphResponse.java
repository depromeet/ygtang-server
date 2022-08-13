package inspiration.domain.inspiration.response;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenGraphResponse {

    private int code;
    private String description;
    private String siteName;
    private String title;
    private String url;
    private String image;


    public static OpenGraphResponse of(int code, String image, String siteName, String title, String url,  String description) {
        return OpenGraphResponse.builder()
                                .code(code)
                                .image(image)
                                .siteName(siteName)
                                .title(title)
                                .url(url)
                                .description(description)
                                .build();
    }

    public static OpenGraphResponse from(int code) {
        return OpenGraphResponse.builder()
                .code(code)
                .build();
    }
}
