package inspiration.application.tag;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.files.FilesUploadRequest;
import com.slack.api.methods.response.files.FilesUploadResponse;
import inspiration.domain.inspiration_tag.InspirationTag;
import inspiration.domain.inspiration_tag.InspirationTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final InspirationTagRepository inspirationTagRepository;

    @Value("${ygtang.slack.token.bot}")
    private String botToken;
    @Value("${ygtang.slack.channel.report}")
    private String reportChannel;

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
                                 .tasklet(tagRankingTasklet())
                                 .transactionManager(new ResourcelessTransactionManager())
                                 .build();
    }

    @Bean
    @StepScope
    public Tasklet tagRankingTasklet() {
        return (contribution, chunkContext) -> {
            List<TagRankingVo> tagRankingVoList = getTagRankingVoList();
            File csvFile = toCsvFile(tagRankingVoList);
            sendFileToSlack(csvFile);
            return RepeatStatus.FINISHED;
        };
    }

    private List<TagRankingVo> getTagRankingVoList() {
        List<InspirationTag> inspirationTags = inspirationTagRepository.findAll();

        Map<String, Set<Long>> contentTagIdSetMap =
                inspirationTags.stream()
                               .collect(Collectors.toMap(
                                       it -> it.getTag().getContent(),
                                       it -> Stream.of(it.getTag().getId()).collect(Collectors.toSet()),
                                       (a, b) -> {
                                           Set<Long> c = new HashSet<>(a);
                                           c.addAll(b);
                                           return c;
                                       }
                               ));
        Map<String, Set<Long>> contentInspirationIdSetMap =
                inspirationTags.stream()
                               .collect(Collectors.toMap(
                                       it -> it.getTag().getContent(),
                                       it -> Stream.of(it.getInspiration().getId()).collect(Collectors.toSet()),
                                       (a, b) -> {
                                           Set<Long> c = new HashSet<>(a);
                                           c.addAll(b);
                                           return c;
                                       }
                               ));
        return contentInspirationIdSetMap.keySet()
                                         .stream()
                                         .map(content -> new TagRankingVo(
                                                 content,
                                                 contentTagIdSetMap.get(content).size(),
                                                 contentInspirationIdSetMap.get(content).size()
                                         ))
                                         .sorted(Comparator.comparing(TagRankingVo::getInspirationCount)
                                                           .thenComparing(TagRankingVo::getTagCount)
                                                           .reversed())
                                         .filter(it -> it.getTagCount() >= 2 || it.getInspirationCount() >= 2)
                                         .collect(Collectors.toList());
    }

    private File toCsvFile(List<TagRankingVo> tagRankingVoList) throws IOException {
        File file = File.createTempFile("tagRanking", "csv");
        file.deleteOnExit();
        FileWriter out = new FileWriter(file);
        CSVFormat csvFormat = CSVFormat.Builder.create()
                                               .setHeader(TagRankingVo.getCsvHeaders())
                                               .build();
        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            for (TagRankingVo tagRankingVo : tagRankingVoList) {
                printer.printRecord(tagRankingVo.getCsvValues());
            }
        }
        return file;
    }

    private void sendFileToSlack(File file) {
        LocalDate today = LocalDate.now();
        try (Slack slack = Slack.getInstance()) {
            MethodsClient methods = slack.methods(botToken);
            FilesUploadResponse response = methods.filesUpload(
                    FilesUploadRequest.builder()
                                      .channels(Collections.singletonList(reportChannel))
                                      .title("Tag ranking at " + today)
                                      .filename("tagRanking_" + today.format(FORMATTER_YYYYMMDD) + ".csv")
                                      .filetype("text/csv")
                                      .file(file)
                                      .build()
            );
            log.info("response: {}", response);
        } catch (Exception e) {
            log.error("Failed to send file to slack", e);
        }
    }

}
