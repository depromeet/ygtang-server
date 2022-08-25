package inspiration.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.connection-timeout:1000}")
    private int connectionTimeout;

    @Value("${cloud.aws.s3.request-timeout:3000}")
    private int requestTimeout;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(connectionTimeout);
        clientConfiguration.setRequestTimeout(requestTimeout);
        return AmazonS3ClientBuilder.standard()
                                    .withRegion(region)
                                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                                    .withClientConfiguration(clientConfiguration)
                                    .build();
    }

}
