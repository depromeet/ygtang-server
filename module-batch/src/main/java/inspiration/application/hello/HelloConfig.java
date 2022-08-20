package inspiration.application.hello;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@ConditionalOnProperty(
        name = "spring.batch.job.names",
        havingValue = HelloConfig.JOB_NAME
)
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class HelloConfig {
    static final String JOB_NAME = "hello-job";
    private static final String STEP_NAME = "hello-step";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(helloStep())
                                .build();
    }

    @Bean
    @JobScope
    public Step helloStep() {
        return stepBuilderFactory.get(STEP_NAME)
                                 .tasklet(helloTasklet())
                                 .transactionManager(new ResourcelessTransactionManager())
                                 .build();
    }

    @Bean
    @StepScope
    public Tasklet helloTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Hello world");
            return RepeatStatus.FINISHED;
        };
    }
}
