package inspiration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(TestRedisConfiguration.class)
public class TestDomainApplication {
}
