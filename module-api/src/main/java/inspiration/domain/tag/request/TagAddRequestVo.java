package inspiration.domain.tag.request;

import inspiration.domain.tag.Tag;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class TagAddRequestVo {
    String content;

    public Tag toEntity() {
        return Tag.builder()
                  .content(this.content)
                  .build();
    }
}
