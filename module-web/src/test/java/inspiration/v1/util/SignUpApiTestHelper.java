package inspiration.v1.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.domain.emailauth.SignUpEmailSendService;
import inspiration.v1.auth.SendEmailRequest;
import inspiration.v1.signup.SignUpRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ClassCanBeRecord")
public class SignUpApiTestHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SignUpEmailSendService signUpEmailSendService;

    public SignUpApiTestHelper(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            SignUpEmailSendService signUpEmailSendService
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.signUpEmailSendService = signUpEmailSendService;
    }

    public MvcResult signUp(
            String email,
            String nickname,
            String password
    ) throws Exception {
        sendVerificationEmail(email);
        confirmEmail(email);
        return submitSignUpRequest(email, nickname, password);
    }

    private void sendVerificationEmail(String email) throws Exception {
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setEmail(email);
        doNothing().when(signUpEmailSendService).send(any(), any());
        mockMvc.perform(
                post("/api/v1/auth/sends-email/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(sendEmailRequest)))
               .andExpect(status().isCreated());
    }

    private void confirmEmail(String email) throws Exception {
        mockMvc.perform(
                get("/api/v1/auth/email/signup")
                        .queryParam("email", email))
               .andExpect(status().isFound());
    }

    private MvcResult submitSignUpRequest(
            String email,
            String nickname,
            String password
    ) throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setNickName(nickname);
        signUpRequest.setPassword(password);
        signUpRequest.setConfirmPassword(password);
        return mockMvc.perform(
                post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpRequest)))
                      .andExpect(status().isCreated())
                      .andReturn();
    }
}
