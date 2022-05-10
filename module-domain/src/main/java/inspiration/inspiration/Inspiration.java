package inspiration.inspiration;

import inspiration.date.BaseTimeEntity;
import inspiration.inspiration_tag.InspirationTag;
import inspiration.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inspiration extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspiration_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private InspirationType type;

    @Lob
    private String content;

    @Lob
    private String memo;

    @OneToMany(mappedBy = "inspiration", fetch = FetchType.LAZY)
    private List<InspirationTag> inspirationTags = new ArrayList<>();

    private Boolean isDeleted;

    private LocalDateTime delDateTime;

    public Inspiration(InspirationType type) {
        this.type = type;
    }

    public void setFilePath(String filePath) {
        this.content = filePath;
    }

    public void remove() {
        this.isDeleted = true;
        this.delDateTime = LocalDateTime.now();
    }

    public void modifyMemo(String memo) {
        this.memo = memo;
    }

    public void writeBy(Member member) {
        this.member = member;
    }

    @PrePersist
    public void prePersist() {
        this.isDeleted = this.isDeleted != null && this.isDeleted;
    }

}
