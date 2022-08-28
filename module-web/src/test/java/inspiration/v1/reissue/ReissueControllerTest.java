package inspiration.v1.reissue;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.TestRedisConfiguration;
import inspiration.auth.TokenResponseVo;
import inspiration.auth.jwt.JwtProvider;
import inspiration.domain.emailauth.EmailAuthRepository;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.MemberService;
import inspiration.domain.member.request.SignUpRequestVo;
import inspiration.signup.SignupService;
import inspiration.v1.auth.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class ReissueControllerTest {
    private static final String EMAIL = "localpart@domain.com";
    private static final String NICKNAME = "nickname";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SignupService signupService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private EmailAuthRepository emailAuthRepository;
    private Long memberId;
    private String refreshToken;

    @BeforeEach
    void setUp() throws Exception {
        when(emailAuthRepository.existsByEmail(any())).thenReturn(true);
        TokenResponseVo tokenResponseVo = signupService.signUp(
                new SignUpRequestVo(
                        EMAIL,
                        "nickname",
                        "password",
                        "password"
                )
        );
        memberId = tokenResponseVo.getMemberId();
        refreshToken = tokenResponseVo.getRefreshToken();
        verify(emailAuthRepository, only()).existsByEmail(any());
    }

    @DisplayName("리프레시 토큰 재발급")
    @Test
    void reissue() throws Exception {
        // given
        // when
        mockMvc.perform(
                post("/api/v1/reissue")
                        .header("REFRESH-TOKEN", refreshToken))
               // then
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
               .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
               .andExpect(jsonPath("$.data.accessTokenExpireDate").isNotEmpty())
               .andExpect(jsonPath("$.data.memberId").value(memberId));
    }
}