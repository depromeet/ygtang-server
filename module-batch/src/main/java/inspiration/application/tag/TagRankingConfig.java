package inspiration.application.tag;

import inspiration.application.slack.SlackService;
import inspiration.domain.inspiration_tag.InspirationTagRepository;
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
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConditionalOnProperty(
        name = "spring.batch.job.names",
        havingValue = TagRankingConfig.JOB_NAME
)
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class TagRankingConfig {
    static final String JOB_NAME = "tag-ranking-job";
    private static final String STEP_NAME = "tag-ranking-step";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job tagRankingJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(tagRankingStep())
                                .build();
    }

    @Bean
    @JobScope
    public Step tagRankingStep() {
        return stepBuilderFactory.get(STEP_NAME)
                                 .tasklet(tagRankingTasklet(null, null, null))
                                 .transactionManager(new ResourcelessTransactionManager())
                                 .build();
    }

    @Bean
    @StepScope
    public Tasklet tagRankingTasklet(
            InspirationTagRepository inspirationTagRepository,
            TagGroupService googleSheetTagGroupService,
            SlackService slackService
    ) {
        return new TagRankingTasklet(
                inspirationTagRepository,
                googleSheetTagGroupService,
                slackService
        );
    }
}
