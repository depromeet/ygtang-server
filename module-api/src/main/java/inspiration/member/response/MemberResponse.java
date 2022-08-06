package inspiration.member.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import inspiration.member.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private Long id;
    private String nickName;
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDateTime;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                            .id(member.getId())
                            .nickName(member.getNickname())
                            .email(member.getEmail())
                            .createdDateTime(member.getCreatedDateTime())
                            .build();
    }
}
