package inspiration.application.inspiration;

import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.inspiration.InspirationRepository;
import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.exception.ResourceNotFoundException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@ConditionalOnProperty(
        name = "spring.batch.job.names",
        havingValue = InspirationConfig.JOB_NAME
)
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class InspirationConfig {
    static final String JOB_NAME = "inspiration-job";
    private static final String STEP_NAME = "inspiration-step";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;
    private final InspirationRepository inspirationRepository;

    @Bean
    public Job InspirationJob(Step memberInfoStep) {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(memberInfoStep)
                                .build();
    }

    @Bean
    @JobScope
    public Step InspirationStep(Tasklet memberInfoTasklet) {
        return stepBuilderFactory.get(STEP_NAME)
                                 .tasklet(memberInfoTasklet)
                                 .transactionManager(new ResourcelessTransactionManager())
                                 .build();
    }

    @Bean
    @StepScope
    public Tasklet InspirationTasklet(
            @Value("#{jobParameters['memberId']}") Long memberId
    ) {
        return (contribution, chunkContext) -> {
            // member
            Member member = memberRepository.findByMemberId(memberId)
                                            .orElseThrow(() -> new ResourceNotFoundException("Member not found. memberId: " + memberId));
            log.info("member: {}", member);

            // inspiration
            Page<Inspiration> inspirationPage = inspirationRepository.findDistinctByMemberIdAndTagIdInAndTypeAndCreatedDateTimeBetween(
                    memberId,
                    Collections.emptyList(),
                    Arrays.stream(InspirationType.values()).toList(),
                    null,
                    LocalDateTime.now(),
                    PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdDateTime"))
            );
            log.info("inspirationPage: {}", inspirationPage);
            log.info("inspirations: {}", inspirationPage.getContent());
            return RepeatStatus.FINISHED;
        };
    }

}
