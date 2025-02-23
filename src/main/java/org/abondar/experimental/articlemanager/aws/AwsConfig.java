package org.abondar.experimental.articlemanager.aws;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@AllArgsConstructor
public class AwsConfig {

    private final AwsProperties awsProperties;

    @Bean
    public S3Client s3Client() {

        var s3client = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()));

        if (awsProperties.isLocal()) {
            s3client.endpointOverride(URI.create(awsProperties.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(awsProperties.getUsername(), awsProperties.getPassword())
                    ));
        } else {
            s3client.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return s3client.build();
    }
}
