package inspiration.domain.inspiration.request;

import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.inspiration.InspirationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@ApiModel("Sample Request InspirationAddRequest")
@Data
public class InspirationAddRequest {

    @ApiModelProperty( notes = "IMAGE | LINK | TEXT")
    @NotNull
    private InspirationType type;
    @ApiModelProperty(notes = "LINK, TEXT의 경우 해당 데이터 입력")
    private String content;
    @ApiModelProperty( notes = "메모 입력")
    private String memo;

    private MultipartFile file;

    private List<Long> tagIds;

    public Inspiration toEntity() {
        return Inspiration.builder()
                .content(this.content)
                .type(this.type)
                .memo(this.memo)
                .build();
    }

}
