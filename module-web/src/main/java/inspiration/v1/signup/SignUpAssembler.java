package inspiration.v1.signup;

import inspiration.v1.member.ExtraInfoRequest;
import inspiration.domain.member.request.ExtraInfoRequestVo;
import inspiration.v1.member.SignUpRequest;
import inspiration.domain.member.request.SignUpRequestVo;
import org.springframework.stereotype.Component;

@Component
public class SignUpAssembler {
    public SignUpRequestVo toSignUpRequestVo(SignUpRequest signUpRequest) {
        return new SignUpRequestVo(
                signUpRequest.getEmail(),
                signUpRequest.getNickName(),
                signUpRequest.getPassword(),
                signUpRequest.getConfirmPassword()
        );
    }

    public ExtraInfoRequestVo toExtraInfoRequestVo(ExtraInfoRequest extraInfoRequest) {
        return new ExtraInfoRequestVo(
                extraInfoRequest.getGender(),
                extraInfoRequest.getAge(),
                extraInfoRequest.getJob()
        );
    }
}
