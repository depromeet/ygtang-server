package inspiration.application.tag;

import inspiration.application.slack.SlackService;
import inspiration.domain.inspiration_tag.InspirationTag;
import inspiration.domain.inspiration_tag.InspirationTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class TagRankingTasklet implements Tasklet {
    private static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final InspirationTagRepository inspirationTagRepository;
    private final TagGroupService googleSheetTagGroupService;
    private final SlackService slackService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<TagGroup> tagGroups = googleSheetTagGroupService.getTagGroups();
        List<TagRankingVo> tagRankingVoList = getTagRankingVoList(tagGroups);
        File csvFile = toCsvFile(tagRankingVoList);
        sendFileToSlack(csvFile);
        return RepeatStatus.FINISHED;
    }

    private List<TagRankingVo> getTagRankingVoList(List<TagGroup> tagGroups) {
        List<InspirationTag> inspirationTags = inspirationTagRepository.findAll();

        Map<String, Set<Long>> contentTagIdSetMap =
                inspirationTags.stream()
                        .collect(Collectors.toMap(
                                it -> resolveTagName(it.getTag().getContent(), tagGroups),
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
                                it -> resolveTagName(it.getTag().getContent(), tagGroups),
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

    private String resolveTagName(String tagName, List<TagGroup> tagGroups) {
        for (TagGroup tagGroup : tagGroups) {
            if (tagGroup.contains(tagName)) {
                return tagGroup.getName();
            }
        }
        return tagName;
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
        slackService.sendCsv(
                file,
                "Tag ranking at " + today,
                "tagRanking_" + today.format(FORMATTER_YYYYMMDD) + ".csv"
        );
    }
}
