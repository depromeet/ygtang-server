package inspiration.infrastructure.spring;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .defaultMessageConverters()
                .setConnectTimeout(Duration.ofSeconds(1L))
                .setReadTimeout(Duration.ofSeconds(2L))
                .build();
    }
}
