package inspiration.member;

import inspiration.emailauth.EmailAuthRepository;
import inspiration.exception.ConflictRequestException;
import inspiration.member.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("닉네임 중복으로 회원가입에 실패한다.")
    void nicknameIfConflict() {

        String nickName = "영감";

        when(memberRepository.existsByNickname(nickName)).thenReturn(true);

        assertThrows(ConflictRequestException.class, () -> memberService.checkNickName(nickName));

        verify(memberRepository, atLeastOnce()).existsByNickname(nickName);
    }
}
