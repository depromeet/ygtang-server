package inspiration.v1.member;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.TestRedisConfiguration;
import inspiration.aws.AwsS3Service;
import inspiration.domain.emailauth.ResetPasswordEmailSendService;
import inspiration.domain.emailauth.SignUpEmailSendService;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.passwordauth.PasswordAuth;
import inspiration.domain.passwordauth.PasswordAuthRepository;
import inspiration.v1.ResultResponse;
import inspiration.v1.auth.SendEmailRequest;
import inspiration.v1.auth.TokenResponse;
import inspiration.v1.util.MvcResultHelper;
import inspiration.v1.util.SignUpApiTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {
    private static final String EMAIL = "localpart@domain.com";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "password";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private SignUpEmailSendService signUpEmailSendService;
    @MockBean
    private ResetPasswordEmailSendService resetPasswordEmailSendService;
    @MockBean
    private PasswordAuthRepository passwordAuthRepository;
    @MockBean
    private AwsS3Service awsS3Service;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        var signUpApiTestHelper = new SignUpApiTestHelper(mockMvc, objectMapper, signUpEmailSendService);
        MvcResult signUpResult = signUpApiTestHelper.signUp(EMAIL, NICKNAME, PASSWORD);
        var response = MvcResultHelper.parse(
                signUpResult,
                new TypeReference<ResultResponse<TokenResponse>>() {
                }
        );
        accessToken = response.getData().getAccessToken();
    }

    @DisplayName("패스워드 변경")
    @Test
    void changePassword() throws Exception {
        // given
        var changedPassword = "changedPassword";
        var updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setPassword(changedPassword);
        updatePasswordRequest.setConfirmPassword(changedPassword);
        // when
        mockMvc.perform(
                put("/api/v1/members/passwords/change")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updatePasswordRequest)))
               // then
               .andExpect(status().isOk());
    }

    @DisplayName("닉네임 변경: 성공")
    @Test
    void changeNickname() throws Exception {
        // given
        String updatedNickname = "updatedNickname";
        var updateNicknameRequest = new UpdateNicknameRequest();
        updateNicknameRequest.setNickname(updatedNickname);
        // when
        mockMvc.perform(
                put("/api/v1/members/nickname/change")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateNicknameRequest)))
               // then 1
               .andExpect(status().isOk());
        // then 2
        Member member = memberRepository.findByEmail(EMAIL).orElseThrow(AssertionError::new);
        assertThat(member.getNickname()).isEqualTo(updatedNickname);
    }

    @DisplayName("닉네임 변경: 이미 사용중인 닉네임인 경우 실패")
    @Test
    void changeNickname_duplicated() throws Exception {
        // given
        String duplicatedNickname = "nickname";
        var updateNicknameRequest = new UpdateNicknameRequest();
        updateNicknameRequest.setNickname(duplicatedNickname);
        // when
        mockMvc.perform(
                put("/api/v1/members/nickname/change")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateNicknameRequest)))
               // then 1
               .andExpect(status().isConflict());
        // then 2
        Member member = memberRepository.findByEmail(EMAIL).orElseThrow(AssertionError::new);
        assertThat(member.getNickname()).isEqualTo(NICKNAME);
    }

    @DisplayName("초기화된 비밀번호 이메일 전송")
    @Test
    void sendEmailContainingResetPassword() throws Exception {
        // given
        var sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setEmail(EMAIL);
        doNothing().when(resetPasswordEmailSendService).send(anyString(), anyString());
        when(passwordAuthRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(new PasswordAuth(EMAIL, true)));
        // when
        mockMvc.perform(
                post("/api/v1/members/sends-email/reset-passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(sendEmailRequest)))
               // then 1
               .andExpect(status().isCreated());
        // then 2
        verify(resetPasswordEmailSendService, only()).send(anyString(), anyString());
        verify(passwordAuthRepository, times(1)).findByEmail(anyString());
        verify(passwordAuthRepository, times(1)).delete(any());
    }

    @DisplayName("사용자 정보 조회")
    @Test
    void getMemberInfo() throws Exception {
        mockMvc.perform(
                get("/api/v1/members/info")
                        .header("accessToken", accessToken))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nickName").value(NICKNAME))
               .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @DisplayName("계정 삭제")
    @Test
    void removeMember() throws Exception {
        mockMvc.perform(
                delete("/api/v1/members/remove")
                        .header("accessToken", accessToken))
               .andExpect(status().isOk());
    }
}