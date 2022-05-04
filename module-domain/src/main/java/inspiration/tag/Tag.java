package inspiration.tag;

import inspiration.date.BaseTimeEntity;
import inspiration.inspiration_tag.InspirationTag;
import inspiration.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<InspirationTag> inspirationTags;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 100)
    private String content;

    public void writeBy(Member member) {
        this.member = member;
    }

//    public void taggedBy(Inspiration inspiration) {
//        this.inspirations.add(inspiration);
//        inspiration.tagInspiration(this);
//    }

}
