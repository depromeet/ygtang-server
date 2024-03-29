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

    @Column
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    /**
     * iOS 에서 나이가 필수값이라 때문에 리젝당함 (2023-04-04)
     */
    @Column
    @Enumerated(EnumType.STRING)
    private AgeGroupType ageGroup;

    @Column
    private String job;

    public void updatePassword(String password) {

        this.password = password;
    }

    public void updateNickname(String nickname) {

        this.nickname = nickname;
    }

    public void updateExtraInfo(GenderType gender, AgeGroupType ageGroup, String job) {

        this.gender = gender;
        this.ageGroup = ageGroup;
        this.job = job;
    }

    public boolean isSameMember(Long id) {
        return this.id.equals(id);
    }
}
