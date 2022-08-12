package inspiration.domain.inspiration.opengraph;

import java.util.Optional;

public interface OpenGraphService {
    Optional<OpenGraphVo> getMetadata(String url);
}