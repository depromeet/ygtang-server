package inspiration.v1.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.TestRedisConfiguration;
import inspiration.domain.emailauth.SignUpEmailSendService;
import inspiration.v1.auth.SendEmailRequest;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.v1.member.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class SignUpControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignUpEmailSendService signUpEmailSendService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 가입")
    @Test
    void signUp() throws Exception {
        // given
        String email = "localpart@domain";
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setEmail(email);
        doNothing().when(signUpEmailSendService).send(any(), any());
        mockMvc.perform(
                post("/api/v1/auth/sends-email/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(sendEmailRequest)))
               .andExpect(status().isCreated());
        mockMvc.perform(
                get("/api/v1/auth/email/signup")
                        .queryParam("email", email))
               .andExpect(status().isFound());
        // when
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setNickName("nickname");
        signUpRequest.setPassword("password");
        signUpRequest.setConfirmPassword("password");
        mockMvc.perform(
                post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpRequest)))
               // then 1
               .andExpect(status().isCreated());
        // then 2
        Optional<Member> member = memberRepository.findByEmail(email);
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo(email);
        assertThat(member.get().getNickname()).isEqualTo("nickname");
    }
}