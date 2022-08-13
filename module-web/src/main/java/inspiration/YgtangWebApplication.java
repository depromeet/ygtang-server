package inspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan("inspiration.property")
public class YgtangWebApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,web");
        SpringApplication.run(YgtangWebApplication.class, args);
    }
}
