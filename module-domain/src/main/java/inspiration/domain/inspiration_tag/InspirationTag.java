package inspiration.domain.inspiration_tag;

import inspiration.domain.date.BaseTimeEntity;
import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.tag.Tag;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InspirationTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspiration_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inspiration_id")
    private Inspiration inspiration;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public InspirationTag(Inspiration inspiration, Tag tag) {
        this.inspiration = inspiration;
        this.tag = tag;
    }

    public static InspirationTag of(Inspiration inspiration, Tag tag) {
        return new InspirationTag(inspiration, tag);
    }
}
