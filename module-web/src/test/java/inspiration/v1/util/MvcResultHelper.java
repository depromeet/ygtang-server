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
            Class<T> clazz
    ) throws IOException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsByteArray(), clazz);
    }

    public static <T> T parse(
            MvcResult mvcResult,
            TypeReference<T> typeReference
    ) throws IOException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsByteArray(), typeReference);
    }

}
