package inspiration.application.member;

import inspiration.domain.inspiration.Inspiration;
import inspiration.domain.inspiration.InspirationRepository;
import inspiration.domain.inspiration.InspirationType;
import inspiration.domain.member.AgeGroupType;
import inspiration.domain.member.GenderType;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private final InspirationRepository inspirationRepository;

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
//            Member member = memberRepository.findByMemberId(memberId)
//                                            .orElseThrow(() -> new ResourceNotFoundException("Member not found. memberId: " + memberId));
//            log.info("member: {}", member);
//
//            Page<Member> memberPage = memberRepository.findBy(
//                    "seong0428@gmail.com",
//                    null,
//                    Collections.emptyList(),
//                    null,
//                    LocalDateTime.now(),
//                    PageRequest.of(0, 10)
//            );
//            log.info("page: {}", memberPage);
//            log.info("members: {}", memberPage.getContent());

//            List<Member> allMembers = memberRepository.findAll();
//            log.info("all members: {}", allMembers);

            Page<Inspiration> inspirationPage = inspirationRepository.findBy(
                    Collections.singletonList(1L),
                    Collections.singletonList(InspirationType.IMAGE),
                    null,
                    LocalDateTime.now(),
                    PageRequest.of(0, 10)
            );
            log.info("page: {}", inspirationPage);
            log.info("inspirations: {}", inspirationPage.getContent());

            return RepeatStatus.FINISHED;
        };
    }
}
