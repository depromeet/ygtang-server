package inspiration.tag.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import inspiration.member.response.MemberResponse;
import inspiration.tag.Tag;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResponse {

    private Long id;
    private MemberResponse memberResponse;
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDatetime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedDatetime;

    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
                                    .id(tag.getId())
                                    .content(tag.getContent())
                                    .createdDatetime(tag.getCreatedDateTime())
                                    .updatedDatetime(tag.getUpdatedDateTime())
                                    .memberResponse(MemberResponse.of(tag.getMember()))
                                    .build();
    }


}
