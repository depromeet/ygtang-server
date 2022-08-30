package inspiration.v1.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

public class MvcResultHelper {
    private MvcResultHelper() {
    }

    public static <T> T parse(
            MvcResult mvcResult,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) throws IOException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), clazz);
    }

    public static <T> T parse(
            MvcResult mvcResult,
            ObjectMapper objectMapper,
            TypeReference<T> typeReference
    ) throws IOException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), typeReference);
    }

}
