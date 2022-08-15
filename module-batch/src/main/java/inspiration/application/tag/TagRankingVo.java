package inspiration.application.tag;

import lombok.Value;

import java.lang.reflect.Field;
import java.util.Arrays;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class TagRankingVo {
    String tagName;
    Integer inspirationCount;
    Integer tagCount;

    public static String[] getCsvHeaders() {
        return Arrays.stream(TagRankingVo.class.getDeclaredFields())
                     .map(Field::getName)
                     .toArray(String[]::new);
    }

    public Object[] getCsvValues() {
        return new Object[]{tagName, inspirationCount, tagCount};
    }
}
