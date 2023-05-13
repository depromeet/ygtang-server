package inspiration.v1.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.domain.emailauth.SignUpEmailSendService;
import inspiration.domain.emailauth.request.SendEmailRequest;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SignUpControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignUpEmailSendService signUpEmailSendService;

    @Autowired
    private MemberRepository memberRepository;

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

    @DisplayName("회원 추가정보 수정: ageGroup이 null 이어도 성공한다.")
    @Test
    void testAgeGroupIsNull() throws Exception {
        // given
        String email = "localpart@domain";
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setEmail(email);
        doNothing().when(signUpEmailSendService).send(any(), any());
        mockMvc.perform(post("/api/v1/auth/sends-email/signup")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsBytes(sendEmailRequest)))
               .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/auth/email/signup")
                       .queryParam("email", email))
               .andExpect(status().isFound());
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setNickName("nickname");
        signUpRequest.setPassword("password");
        signUpRequest.setConfirmPassword("password");
        mockMvc.perform(
                       post("/api/v1/signup")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsBytes(signUpRequest)))
               .andExpect(status().isCreated());
        // when
        mockMvc.perform(patch("/api/v1/signup/extra-informations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"gender\":\"ETC\",\"ageGroup\":null,\"job\":\"job\",\"email\":\""+email+"\"}")
                // then
        ).andExpect(status().isOk());
    }

    @DisplayName("회원가입 email 검증: email에 + 문자가 포함되어 있어도 성공한다.")
    @Test
    void signUpEmailValid() throws Exception {
        // given
        String email = "localpart+01@domain";
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
                                .queryParam("email",email))
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
