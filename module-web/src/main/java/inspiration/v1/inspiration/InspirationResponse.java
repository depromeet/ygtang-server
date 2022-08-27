package inspiration.v1.inspiration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import inspiration.domain.inspiration.InspirationType;
import inspiration.v1.member.MemberResponse;
import inspiration.v1.tag.TagResponse;
import inspiration.v1.inspiration.opengraph.OpenGraphResponse;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuppressWarnings("ClassCanBeRecord")
public class InspirationResponse {

    private final Long id;
    private final MemberResponse memberResponse;
    private final List<TagResponse> tagResponses;
    private final InspirationType type;
    private final String content;
    private final String memo;
    private final OpenGraphResponse openGraphResponse;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime createdDatetime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime updatedDatetime;
}
