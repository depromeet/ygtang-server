package inspiration.v1.tag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.TestRedisConfiguration;
import inspiration.domain.emailauth.SignUpEmailSendService;
import inspiration.domain.tag.TagRepository;
import inspiration.v1.ResultResponse;
import inspiration.v1.auth.TokenResponse;
import inspiration.v1.util.MvcResultHelper;
import inspiration.v1.util.SignUpApiTestHelper;
import inspiration.v1.util.TagApiTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class TagControllerTest {
    private static final String EMAIL = "localpart@domain.com";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SignUpEmailSendService signUpEmailSendService;

    private String accessToken;
    @Autowired
    private TagRepository tagRepository;

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

    @DisplayName("태그 조회")
    @Test
    void listTag() throws Exception {
        // given
        var tagApiTestHelper = new TagApiTestHelper(mockMvc, objectMapper);
        List<Long> tagIds = tagApiTestHelper.addTags(accessToken, "tag1", "tag2", "tag3");
        // when
        var actual = tagApiTestHelper.listTags(accessToken, PageRequest.of(0, 10));
        // then
        assertThat(actual.getData().getTotalElements()).isEqualTo(3L);
        assertThat(actual.getData().getContent()).hasSize(3);
        assertThat(actual.getData().getContent()).map(TagResponse::getId).containsAll(tagIds);
        assertThat(actual.getData().getContent()).map(TagResponse::getContent).contains("tag1", "tag2", "tag3");
    }

    @DisplayName("태그 like 검색")
    @Test
    void indexTag() throws Exception {
        // given
        var tagApiTestHelper = new TagApiTestHelper(mockMvc, objectMapper);
        tagApiTestHelper.addTag(accessToken, "notStartWithTag");
        List<Long> tagIds = tagApiTestHelper.addTags(accessToken, "tag1", "tag2", "tag3");
        // when
        var actual = tagApiTestHelper.indexTag(accessToken, "tag", PageRequest.of(0, 10));
        // then
        assertThat(actual.getData().getTotalElements()).isEqualTo(3L);
        assertThat(actual.getData().getContent()).hasSize(3);
        assertThat(actual.getData().getContent()).map(TagResponse::getId).containsAll(tagIds);
        assertThat(actual.getData().getContent()).map(TagResponse::getContent).contains("tag1", "tag2", "tag3");
    }

    @DisplayName("태그 like 검색")
    @Test
    void searchTag() throws Exception {
        // given
        var tagApiTestHelper = new TagApiTestHelper(mockMvc, objectMapper);
        List<Long> tagIds = tagApiTestHelper.addTags(accessToken, "tag1", "tag2", "tag3");
        // when
        var actual = tagApiTestHelper.searchTag(accessToken, "tag1", PageRequest.of(0, 10));
        // then
        assertThat(actual.getData().getTotalElements()).isEqualTo(1L);
        assertThat(actual.getData().getContent()).hasSize(1);
        assertThat(actual.getData().getContent()).map(TagResponse::getId).contains(tagIds.get(0));
        assertThat(actual.getData().getContent()).map(TagResponse::getContent).contains("tag1");
    }

    @DisplayName("태그 등록")
    @Test
    void addTag() throws Exception {
        // given
        var tagApiTestHelper = new TagApiTestHelper(mockMvc, objectMapper);
        var tagName = "tagName";
        // when
        TagResponse actual = tagApiTestHelper.addTag(accessToken, tagName);
        // then
        assertThat(actual.getContent()).isEqualTo(tagName);
    }

    @DisplayName("태그 삭제")
    @Test
    void removeTag() throws Exception {
        // given
        var tagApiTestHelper = new TagApiTestHelper(mockMvc, objectMapper);
        var tagResponse = tagApiTestHelper.addTag(accessToken, "tagName");
        var tagId = tagResponse.getId();
        // when
        tagApiTestHelper.removeTag(accessToken, tagId);
        // then
        assertThat(tagRepository.findById(tagId)).isEmpty();
    }

    @DisplayName("태그 전체 삭제")
    @Test
    void removeAllTags() throws Exception {
        // given
        var tagApiTestHelper = new TagApiTestHelper(mockMvc, objectMapper);
        List<Long> tagIds = tagApiTestHelper.addTags(accessToken, "tag1", "tag2", "tag3");
        // when
        tagApiTestHelper.removeAllTags(accessToken);
        // then
        assertThat(tagRepository.findAllById(tagIds)).isEmpty();
    }
}