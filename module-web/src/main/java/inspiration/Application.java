package inspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ConfigurationPropertiesScan("inspiration.property")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
