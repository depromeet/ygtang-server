package inspiration.domain.inspiration.request;

import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class InspirationTagRequestVo {
    Long inspirationId;
    Long tagId;
}
