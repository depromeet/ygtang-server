package inspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YgtangWebApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,web");
        SpringApplication.run(YgtangWebApplication.class, args);
    }
}
