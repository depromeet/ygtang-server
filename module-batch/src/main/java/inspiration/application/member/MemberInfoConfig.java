package inspiration.application.member;

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

@Slf4j
@ConditionalOnProperty(
        name = "spring.batch.job.names",
        havingValue = MemberInfoConfig.JOB_NAME
)
@Configuration
@RequiredArgsConstructor
public class MemberInfoConfig {
    static final String JOB_NAME = "member-info-job";
    static final String STEP_NAME = "member-info-step";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;

    @Bean
    public Job memberInfoJob(Step memberInfoStep) {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(memberInfoStep)
                                .build();
    }

    @Bean
    @JobScope
    public Step memberInfoStep(Tasklet memberInfoTasklet) {
        return stepBuilderFactory.get(STEP_NAME)
                                 .tasklet(memberInfoTasklet)
                                 .transactionManager(new ResourcelessTransactionManager())
                                 .build();
    }

    @Bean
    @StepScope
    public Tasklet memberInfoTasklet(
            @Value("#{jobParameters['memberId']}") Long memberId
    ) {
        return (contribution, chunkContext) -> {
            Member member = memberRepository.findByMemberId(memberId)
                                            .orElseThrow(() -> new ResourceNotFoundException("Member not found. memberId: " + memberId));
            log.info("member: {}", member);
            return RepeatStatus.FINISHED;
        };
    }
}
