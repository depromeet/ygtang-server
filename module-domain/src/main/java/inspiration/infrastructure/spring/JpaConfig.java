package inspiration.infrastructure.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    @Bean
    @ConfigurationProperties("ygtang.domain.datasource")
    public HikariConfig ygtangDataSourceProperties() {
        return new HikariConfig();
    }

    @Primary
    @Bean
    public DataSource ygtangDataSource() {
        return new HikariDataSource(ygtangDataSourceProperties());
    }
}
