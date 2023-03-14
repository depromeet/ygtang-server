package inspiration.infrastructure.spring;

import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;

@Configuration
public class ExecutorConfig {
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        return new TaskExecutorBuilder()
                .corePoolSize(12)
                .maxPoolSize(12)
                .queueCapacity(200)
                .awaitTermination(true)
                .awaitTerminationPeriod(Duration.ofSeconds(10))
                .threadNamePrefix("task-executor-")
                .build();
    }
}
