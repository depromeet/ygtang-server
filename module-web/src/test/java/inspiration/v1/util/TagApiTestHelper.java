package inspiration.v1.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspiration.v1.RestPage;
import inspiration.v1.ResultResponse;
import inspiration.v1.tag.TagAddRequest;
import inspiration.v1.tag.TagResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ClassCanBeRecord")
public class TagApiTestHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TagApiTestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ResultResponse<RestPage<TagResponse>> listTags(
            String accessToken,
            Pageable pageable
    ) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/tag/list")
                        .header("accessToken", accessToken)
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize())))
                                     .andReturn();
        return MvcResultHelper.parse(mvcResult, new TypeReference<>() {
        });
    }

    public ResultResponse<RestPage<TagResponse>> indexTag(
            String accessToken,
            String keyword,
            Pageable pageable
    ) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/tag/index/{keyword}", keyword)
                        .header("accessToken", accessToken)
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize())))
                                     .andReturn();
        return MvcResultHelper.parse(mvcResult, new TypeReference<>() {
        });
    }

    public ResultResponse<RestPage<TagResponse>> searchTag(
            String accessToken,
            String keyword,
            Pageable pageable
    ) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/tag/search/{keyword}", keyword)
                        .header("accessToken", accessToken)
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize())))
                                     .andReturn();
        return MvcResultHelper.parse(mvcResult, new TypeReference<>() {
        });
    }

    public TagResponse addTag(
            String accessToken,
            String content
    ) throws Exception {
        var tagAddRequest = new TagAddRequest();
        tagAddRequest.setContent(content);
        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/tag/add")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(tagAddRequest)))
                                     .andReturn();
        return MvcResultHelper.parse(mvcResult, TagResponse.class);
    }

    public List<Long> addTags(
            String accessToken,
            String... tagNames
    ) {
        return Arrays.stream(tagNames)
                     .map(it -> {
                         try {
                             return addTag(accessToken, it);
                         } catch (Exception e) {
                             throw new AssertionError(e);
                         }
                     })
                     .map(TagResponse::getId)
                     .toList();
    }

    public void removeTag(
            String accessToken,
            Long tagId
    ) throws Exception {
        mockMvc.perform(
                delete("/api/v1/tag/remove/{id}", tagId)
                        .header("accessToken", accessToken))
               .andExpect(status().isOk());
    }

    public void removeAllTags(String accessToken) throws Exception {
        mockMvc.perform(
                delete("/api/v1/tag/remove/all")
                        .header("accessToken", accessToken))
               .andExpect(status().isOk());
    }
}
