package inspiration.inspiration.opengraph;

import java.util.Optional;

public interface OpenGraphService {
    Optional<OpenGraphVo> getMetadata(String url);
}