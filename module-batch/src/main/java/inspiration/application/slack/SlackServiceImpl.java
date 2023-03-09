package inspiration.application.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.files.FilesUploadRequest;
import com.slack.api.methods.response.files.FilesUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;

@Slf4j
@Service
public class SlackServiceImpl implements SlackService {
    @Value("${ygtang.slack.token.bot}")
    private String botToken;
    @Value("${ygtang.slack.channel.report}")
    private String reportChannel;

    @Override
    public void sendCsv(File file, String title, String filename) {
        try (Slack slack = Slack.getInstance()) {
            MethodsClient methods = slack.methods(botToken);
            FilesUploadResponse response = methods.filesUpload(
                    FilesUploadRequest.builder()
                            .channels(Collections.singletonList(reportChannel))
                            .title(title)
                            .filename(filename)
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
