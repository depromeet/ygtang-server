package inspiration.infrastructure;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@EnableBatchProcessing
@Configuration
public class BatchConfig {
    @Bean
    @ConfigurationProperties("ygtang.batch.datasource")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource batchDataSource() {
        return batchDataSourceProperties().initializeDataSourceBuilder()
                                          .type(HikariDataSource.class)
                                          .build();
    }

    @Bean
    public BatchConfigurer batchConfigurer() {
        return new DefaultBatchConfigurer(batchDataSource());
    }
}
