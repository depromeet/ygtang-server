package inspiration;

import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class YgtangBatchApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,batch");
        System.exit(SpringApplication.exit(
                new SpringApplicationBuilder()
                        .environment(StandardEncryptableEnvironment.builder().build())
                        .sources(YgtangBatchApplication.class).run(args)
        ));
    }
}
