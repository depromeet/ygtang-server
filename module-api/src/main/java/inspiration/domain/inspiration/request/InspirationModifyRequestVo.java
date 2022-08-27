package inspiration.domain.inspiration.request;

import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class InspirationModifyRequestVo {
    Long id;
    String memo;
}
