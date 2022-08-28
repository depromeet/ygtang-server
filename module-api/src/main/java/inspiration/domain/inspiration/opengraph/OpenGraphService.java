package inspiration.domain.inspiration.opengraph;

import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.inspiration.response.OpenGraphResponseVo;

public interface OpenGraphService {
    OpenGraphResponseVo getOpenGraphResponseVo(InspirationType inspirationType, String link);
}