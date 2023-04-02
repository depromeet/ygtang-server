package inspiration.domain.member;

import inspiration.domain.date.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;


@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private AgeGroupType age_group;

    @Column(nullable = true)
    private String job;

    @Column(nullable = true, length = 1)
    private Integer status;

    public void updatePassword(String password) {

        this.password = password;
    }

    public void updateNickname(String nickname) {

        this.nickname = nickname;
    }

    public void updateExtraInfo(GenderType gender, AgeGroupType age_group, String job) {

        this.gender = gender;
        this.age_group = age_group;
        this.job = job;
    }

    public boolean isSameMember(Long id) {
        return this.id.equals(id);
    }

    public void removeMember(){
        this.status = 1;
    }
}
