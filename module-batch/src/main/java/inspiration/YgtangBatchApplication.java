package inspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YgtangBatchApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,batch");
        System.exit(SpringApplication.exit(SpringApplication.run(YgtangBatchApplication.class, args)));
    }
}
