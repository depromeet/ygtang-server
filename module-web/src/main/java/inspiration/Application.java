package inspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan("inspiration.property")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
