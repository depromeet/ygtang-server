package inspiration.v1.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.TestRedisConfiguration;
import inspiration.domain.emailauth.SignUpEmailSendService;
import inspiration.enumeration.RedisKey;
import inspiration.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisService redisService;
    @MockBean
    private SignUpEmailSendService signUpEmailSendService;

    @DisplayName("인증 메일 발송")
    @Test
    void sendEmailForSignup() throws Exception {
        // given
        String email = "localpart@domain";
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setEmail(email);
        doNothing().when(signUpEmailSendService).send(any(), any());
        // when
        mockMvc.perform(
                post("/api/v1/auth/sends-email/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(sendEmailRequest)))
               // then 1
               .andExpect(status().isCreated());
        // then 2
        String authToken = redisService.getData(RedisKey.EAUTH_SIGN_UP.getKey() + email);
        assertThat(authToken).isNotBlank();
        verify(signUpEmailSendService).send(any(), any());
    }

    @DisplayName("인증 메일 링크 통해 이메일 소유 확인")
    @Test
    void authenticateEmailOfSignUp() throws Exception {
        // given
        String email = "localpart@domain";
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setEmail(email);
        mockMvc.perform(
                post("/api/v1/auth/sends-email/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(sendEmailRequest)))
               .andExpect(status().isCreated());
        // when
        mockMvc.perform(
                get("/api/v1/auth/email/signup")
                        .queryParam("email", email))
               // then
               .andExpect(status().isFound());
    }
}