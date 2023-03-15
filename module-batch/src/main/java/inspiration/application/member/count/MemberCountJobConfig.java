package inspiration.application.member.count;

import inspiration.application.slack.SlackService;
import inspiration.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(
        name = "spring.batch.job.names",
        havingValue = MemberCountJobConfig.JOB_NAME
)
@Configuration
@RequiredArgsConstructor
public class MemberCountJobConfig {
    static final String JOB_NAME = "member-count-job";
    static final String STEP_NAME = "member-count-step";
    private static final int CHUNK_SIZE = 1000;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SlackService slackService;

    @Value("${ygtang.temporary-directory-path}")
    private String temporaryDirectoryPath;

    @Bean
    public Job memberDailyCountJob(Step memberDailyCountStep) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(memberDailyCountStep)
                .build();
    }

    @Bean
    @JobScope
    public Step memberDailyCountStep(
            EntityManagerFactory entityManagerFactory
    ) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Member, Member>chunk(CHUNK_SIZE)
                .reader(new JpaPagingItemReaderBuilder<Member>()
                        .name("memberItemReader")
                        .entityManagerFactory(entityManagerFactory)
                        .queryString("SELECT m FROM Member m")
                        .pageSize(CHUNK_SIZE)
                        .build())
                .writer(items -> {
                    LocalDateTime now = LocalDateTime.now();

                    // daily
                    Map<LocalDate, Integer> dailyCountMap = items.stream()
                            .collect(Collectors.toMap(
                                    it -> it.getCreatedDateTime().toLocalDate(),
                                    it -> 1,
                                    Integer::sum
                            ));
                    slackService.sendCsv(
                            toDailyCsvFile(dailyCountMap),
                            "Member Daily Count at " + now,
                            "member_daily_count_" + now.toLocalDate() + ".csv"
                    );

                    // monthly
                    Map<YearMonth, Integer> monthlyCountMap = items.stream()
                            .collect(Collectors.toMap(
                                    it -> YearMonth.from(it.getCreatedDateTime()),
                                    it -> 1,
                                    Integer::sum
                            ));
                    slackService.sendCsv(
                            toMonthlyCsvFile(monthlyCountMap),
                            "Member Monthly Count at " + now,
                            "member_monthly_count_" + now.toLocalDate() + ".csv"
                    );
                })
                .build();
    }

    private File toDailyCsvFile(Map<LocalDate, Integer> dailyCountMap) throws IOException {
        createDirectoryIfNotExists(temporaryDirectoryPath);
        File file = File.createTempFile("memberDailyCount", "csv", new File(temporaryDirectoryPath));
        file.deleteOnExit();
        FileWriter out = new FileWriter(file);
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader("date", "count")
                .build();
        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            dailyCountMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(it -> {
                        try {
                            printer.printRecord(it.getKey(), it.getValue());
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
            );
        }
        return file;
    }

    private void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }


    private File toMonthlyCsvFile(Map<YearMonth, Integer> monthlyCountMap) throws IOException {
        createDirectoryIfNotExists(temporaryDirectoryPath);
        File file = File.createTempFile("memberMonthlyCount", "csv", new File(temporaryDirectoryPath));
        file.deleteOnExit();
        FileWriter out = new FileWriter(file);
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader("yearMonth", "count")
                .build();
        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            monthlyCountMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(it -> {
                        try {
                            printer.printRecord(it.getKey(), it.getValue());
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
            );
        }
        return file;
    }
}
