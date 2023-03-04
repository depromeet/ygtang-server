package inspiration.application.slack;

import java.io.File;

public interface SlackService {
    void sendCsv(File file, String title, String filename);
}
