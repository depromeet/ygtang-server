package inspiration.v1.inspiration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.ResultResponse;
import inspiration.TestMysqlConfiguration;
import inspiration.auth.AuthService;
import inspiration.auth.TokenResponse;
import inspiration.domain.emailauth.EmailAuthRepository;
import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.inspiration.request.InspirationAddRequest;
import inspiration.domain.inspiration.request.InspirationTagRequest;
import inspiration.domain.member.request.SignUpRequest;
import inspiration.domain.tag.request.TagAddRequest;
import inspiration.enumeration.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TestMysqlConfiguration.class})
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class InspirationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private EmailAuthRepository emailAuthRepository;

    TokenResponse tokenResponse;

    @BeforeEach
    void before() throws Exception {
        // given
        String email = "localpart@domain";
        String password = "localpart";
        String nickname = "nickname";

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setConfirmPassword(password);
        signUpRequest.setNickName(nickname);

        when(emailAuthRepository.existsByEmail(email)).thenReturn(true);

        // when
        String resString = mockMvc.perform(
                       post("/api/v1/signup")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsBytes(signUpRequest)))
                // then 1
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()
                ;

        ObjectMapper objectMapper = new ObjectMapper();
        tokenResponse = objectMapper.readValue(resString, new TypeReference<ResultResponse<TokenResponse>>() {}).getData();
    }

    @DisplayName("영감 등록 테스트")
    @Test
    void addInspirationTest() throws Exception {
        addInspiration(null);
    }

    @DisplayName("영감 조회 테스트")
    @Test
    void findInspirationTest() throws Exception {
        Long id = addInspiration(null);
        String uri = "/api/v1/inspiration/".concat(String.valueOf(id));

        //given
        //when
        ResultActions perform = mockMvc.perform(
                        get(uri)
                        .header(TokenType.ACCESS_TOKEN.getMessage(), tokenResponse.getAccessToken()));
        //then
        perform
                .andDo(MockMvcResultHandlers.log())
                .andExpect(status().isOk());
    }

    @DisplayName("영감 태깅 테스트")
    @Test
    void tagInspirationTest() throws Exception {
        Long id = addInspiration(null);
        Long tagId = addTag();
        String uri = "/api/v1/inspiration/tag";

        //given
        InspirationTagRequest inspirationTagRequest = new InspirationTagRequest();
        inspirationTagRequest.setId(id);
        inspirationTagRequest.setTagId(tagId);

        //when
        ResultActions perform = mockMvc.perform(
                post(uri)
                        .header(TokenType.ACCESS_TOKEN.getMessage(), tokenResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(inspirationTagRequest)));
        //then
        perform
                .andDo(MockMvcResultHandlers.log())
                .andExpect(status().isCreated());
    }


    @DisplayName("태그 등록 테스트")
    @Test
    void addTagTest() throws Exception {
        addTag();
    }

    @DisplayName("영감 언태깅")
    @Test
    void untagInspirationTest() throws Exception {
        Long tagId = addTag();
        Long id = addInspiration(List.of(tagId));

        String uri = "/api/v1/inspiration/untag/".concat(String.valueOf(id)).concat("/").concat(String.valueOf(tagId));

        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete(uri)
                        .header(TokenType.ACCESS_TOKEN.getMessage(), tokenResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        perform
                .andDo(MockMvcResultHandlers.log())
                .andExpect(status().isOk());
    }


    private Long addInspiration(List<Long> tagIds) throws Exception {

        String uri = "/api/v1/inspiration/add";
        //given
        InspirationAddRequest inspirationAddRequest = new InspirationAddRequest();
        inspirationAddRequest.setContent("test");
        inspirationAddRequest.setType(InspirationType.TEXT);
        inspirationAddRequest.setMemo("memo test");
        inspirationAddRequest.setTagIds(tagIds);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.addAll("tagIds",
                                            Stream.ofNullable(tagIds)
                                                  .flatMap(Collection::stream)
                                                  .map(String::valueOf).collect(Collectors.toList()));

        //when
        ResultActions perform = mockMvc.perform(
                post(uri)
                        .header(TokenType.ACCESS_TOKEN.getMessage(), tokenResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content", inspirationAddRequest.getContent())
                        .param("type", InspirationType.TEXT.name())
                        .param("memo", inspirationAddRequest.getMemo())
                        .params(multiValueMap));

        //then
        perform.andDo(MockMvcResultHandlers.print())
               .andExpect(status().isCreated());
        MockHttpServletResponse returnURi = perform.andReturn().getResponse();
        return Long.valueOf(Objects.requireNonNull(returnURi.getRedirectedUrl()).replaceAll(uri, "").replaceAll("/", ""));
    }
    private  Long addTag() throws Exception {

        String uri = "/api/v1/tag/add";
        //given
        TagAddRequest tagAddRequest = new TagAddRequest();
        tagAddRequest.setContent("tag test");
        //when
        ResultActions perform = mockMvc.perform(
                post(uri)
                        .header(TokenType.ACCESS_TOKEN.getMessage(), tokenResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(tagAddRequest)));

        //then
        perform.andDo(MockMvcResultHandlers.print())
               .andExpect(status().isCreated());
        MockHttpServletResponse returnURi = perform.andReturn().getResponse();
        return Long.valueOf(Objects.requireNonNull(returnURi.getRedirectedUrl()).replaceAll(uri, "").replaceAll("/", ""));
    }

}