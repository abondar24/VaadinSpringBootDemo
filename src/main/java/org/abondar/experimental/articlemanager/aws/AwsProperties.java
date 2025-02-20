package org.abondar.experimental.articlemanager.aws;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="aws")
@Getter
@Setter
public class AwsProperties {

    private String endpoint;
    private String username;
    private String password;
    private String region;
    private String s3Bucket;
}
