package inspiration.domain.inspiration.request;

import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.inspiration.InspirationType;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class InspirationAddRequestVo {
    InspirationType type;
    String content;
    String memo;
    MultipartFile file;
    List<Long> tagIds;

    public Inspiration toEntity() {
        return Inspiration.builder()
                          .content(this.content)
                          .type(this.type)
                          .memo(this.memo)
                          .build();
    }
}
