package inspiration.inspiration.request;

import inspiration.inspiration.Inspiration;
import inspiration.inspiration.InspirationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Getter
@ApiModel("Sample Request InspirationAddRequest")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InspirationAddRequest {

    @ApiModelProperty( notes = "IMAGE | LINK | TEXT")
    private InspirationType type;
    @ApiModelProperty(notes = "LINK, TEXT의 경우 해당 데이터 입력")
    private String content;
    @ApiModelProperty( notes = "메모 입력")
    private String memo;

    private MultipartFile file;

    public Inspiration toEntity() {
        return Inspiration.builder()
                .content(this.content)
                .type(this.type)
                .memo(this.memo)
                .build();
    }

}
