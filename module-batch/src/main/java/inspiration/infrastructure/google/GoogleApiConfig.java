package inspiration.infrastructure.google;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

@Configuration
public class GoogleApiConfig {
    @Value("${ygtang.google.api-key}")
    private String googleApiKey;

    @Bean
    public RestTemplate googleApiRestTemplate() {
        return new RestTemplateBuilder()
                       .additionalInterceptors(
                               (request, body, execution) -> {
                                   URI uri = UriComponentsBuilder.fromHttpRequest(request)
                                                                 .queryParam("key", googleApiKey)
                                                                 .build().toUri();

                                   HttpRequest modifiedRequest = new HttpRequestWrapper(request) {
                                       @NotNull
                                       @Override
                                       public URI getURI() {
                                           return uri;
                                       }
                                   };
                                   return execution.execute(modifiedRequest, body);
                               }
                       )
                       .setConnectTimeout(Duration.ofSeconds(1L))
                       .setReadTimeout(Duration.ofSeconds(3L))
                       .build();
    }
}
