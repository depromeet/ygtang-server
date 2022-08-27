package inspiration.v1.member;

import inspiration.domain.member.response.MemberInfoVo;
import inspiration.domain.member.response.MemberResponseVo;
import org.springframework.stereotype.Component;

@Component
public class MemberAssembler {
    public MemberInfoResponse toMemberInfoResponse(MemberInfoVo memberInfoVo) {
        return new MemberInfoResponse(
                memberInfoVo.getNickName(),
                memberInfoVo.getEmail()
        );
    }

    public MemberResponse toMemberResponse(MemberResponseVo memberResponseVo) {
        return new MemberResponse(
                memberResponseVo.getId(),
                memberResponseVo.getNickName(),
                memberResponseVo.getEmail(),
                memberResponseVo.getCreatedDateTime()
        );
    }
}
