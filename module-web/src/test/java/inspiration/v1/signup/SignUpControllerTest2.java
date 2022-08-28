package inspiration.v1.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.TestRedisConfiguration;
import inspiration.domain.emailauth.EmailAuthRepository;
import inspiration.domain.member.*;
import inspiration.domain.member.request.SignUpRequestVo;
import inspiration.signup.SignupService;
import inspiration.v1.member.ExtraInfoRequest;
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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
@Transactional
public class SignUpControllerTest2 {
    private static final String EMAIL = "localpart@domain.com";
    private static final String NICKNAME = "nickname";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EmailAuthRepository emailAuthRepository;
    @Autowired
    private SignupService signupService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    private Long memberId;

    @BeforeEach
    void setUp() throws Exception {
        when(emailAuthRepository.existsByEmail(any())).thenReturn(true);
        memberId = signupService.signUp(
                new SignUpRequestVo(
                        EMAIL,
                        "nickname",
                        "password",
                        "password"
                )
        ).getMemberId();
        verify(emailAuthRepository, only()).existsByEmail(any());
    }

    @DisplayName("사용자 추가 정보를 저장")
    @Test
    void updateExtraInfo() throws Exception {
        // given
        GenderType genderType = GenderType.MALE;
        AgeGroupType ageGroupType = AgeGroupType.EARLY_30S;
        String job = "job";
        ExtraInfoRequest extraInfoRequest = new ExtraInfoRequest();
        extraInfoRequest.setGender(genderType);
        extraInfoRequest.setAge(ageGroupType);
        extraInfoRequest.setJob(job);
        // when
        mockMvc.perform(
                patch("/api/v1/signup/extra-informations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(extraInfoRequest))
                        .queryParam("email", EMAIL))
               // then 1
               .andExpect(status().isOk());
        // then 2
        Member member = memberRepository.findById(memberId).orElseThrow(AssertionError::new);
        assertThat(member.getGender()).isEqualTo(genderType);
        assertThat(member.getAge_group()).isEqualTo(ageGroupType);
        assertThat(member.getJob()).isEqualTo(job);
    }

    @DisplayName("닉네임 중복 확인: 사용가능")
    @Test
    void checkNickname_available() throws Exception {
        // given
        String availableNickname = "availableNickname";
        // when
        mockMvc.perform(get("/api/v1/signup/nicknames/{nickname}/exists", availableNickname))
               // then
               .andExpect(status().isOk());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @DisplayName("닉네임 중복 확인: 사용중")
    @Test
    void checkNickname_duplicated() throws Exception {
        // given
        String duplicatedNickname = NICKNAME;
        // when
        mockMvc.perform(get("/api/v1/signup/nicknames/{nickname}/exists", duplicatedNickname))
               // then
               .andExpect(status().isConflict());
    }

    @DisplayName("이메일 가입 여부 확인: 가입 가능")
    @Test
    void validSignUpEmailStatus_available() throws Exception {
        // given
        String availableEmail = "availableEmail@domain.com";
        // when
        mockMvc.perform(get("/api/v1/signup/{email}/status", availableEmail))
               // then
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data").value(false));
    }

    @DisplayName("이메일 가입 여부 확인: 사용중")
    @Test
    void validSignUpEmailStatus_alreadyUsed() throws Exception {
        // given
        String alreadyUsedEmail = EMAIL;
        // when
        mockMvc.perform(get("/api/v1/signup/{email}/status", alreadyUsedEmail))
               // then
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data").value(true));
    }
}
