package inspiration.domain.inspiration.response;

import inspiration.aws.AwsS3Service;
import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.member.response.MemberResponseVo;
import inspiration.domain.tag.response.TagResponseVo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("ClassCanBeRecord")
public class InspirationResponseVo {
    private final Long id;
    private final MemberResponseVo memberResponseVo;
    private final List<TagResponseVo> tagResponseVoList;
    private final InspirationType type;
    private final String content;
    private final String memo;
    private final LocalDateTime createdDateTime;
    private final LocalDateTime updatedDateTime;

    public static InspirationResponseVo of(
            Inspiration inspiration,
            AwsS3Service awsS3Service
    ) {
        return InspirationResponseVo.builder()
                                    .id(inspiration.getId())
                                    .type(inspiration.getType())
                                    .content(getFilePath(inspiration, awsS3Service))
                                    .memo(inspiration.getMemo())
                                    .createdDateTime(inspiration.getCreatedDateTime())
                                    .updatedDateTime(inspiration.getUpdatedDateTime())
                                    .memberResponseVo(MemberResponseVo.of(inspiration.getMember()))
                                    .tagResponseVoList(inspiration.getInspirationTags().stream()
                                                                  .map(it -> TagResponseVo.from(it.getTag()))
                                                                  .collect(Collectors.toList()))
                                    .build();
    }

    private static String getFilePath(Inspiration inspiration, AwsS3Service awsS3Service) {
        return inspiration.getType() == InspirationType.IMAGE
                ? awsS3Service.getFilePath(inspiration.getContent())
                : inspiration.getContent();
    }
}
