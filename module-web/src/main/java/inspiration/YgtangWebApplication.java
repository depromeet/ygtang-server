package inspiration;

import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class YgtangWebApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,web");
        new SpringApplicationBuilder()
                .environment(StandardEncryptableEnvironment.builder().build())
                .sources(YgtangWebApplication.class).run(args);
    }
}
