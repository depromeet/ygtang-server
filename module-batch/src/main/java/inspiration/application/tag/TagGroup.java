package inspiration.application.tag;

import org.springframework.util.Assert;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class TagGroup {
    private final String name;
    private final List<String> candidates;

    private TagGroup(String name, List<String> candidates) {
        this.name = name;
        this.candidates = candidates;
    }

    public static TagGroup from(List<String> candidates) {
        Assert.notEmpty(candidates, "'candidates' must not be null or empty");
        return new TagGroup(candidates.get(0), candidates);
    }

    public String getName() {
        return name;
    }

    public boolean contains(String name) {
        return candidates.stream().anyMatch(it -> it.equalsIgnoreCase(name));
    }
}
