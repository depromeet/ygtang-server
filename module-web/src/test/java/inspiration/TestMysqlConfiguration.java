package inspiration;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.ZoneId;
import java.util.TimeZone;

import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;

@TestConfiguration
public class TestMysqlConfiguration {

    private static EmbeddedMysql server;

    static {
        MysqldConfig config = aMysqldConfig(Version.v5_7_latest)
                .withCharset(Charset.aCharset("utf8mb4", "utf8mb4_bin"))
                .withPort(3308)
                .withTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .withServerVariable("sql_mode", "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION")
                .withServerVariable("max_connect_errors", 666)
                .build();

        server = EmbeddedMysql.anEmbeddedMysql(config)
                              .addSchema("inspiration")
                              .start();
    }


}
