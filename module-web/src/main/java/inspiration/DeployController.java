package inspiration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {

    @GetMapping("/health")
    public String checkHealth() {
        return "healthy";
    }

}
