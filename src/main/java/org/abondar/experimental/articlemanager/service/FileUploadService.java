package org.abondar.experimental.articlemanager.service;

import lombok.AllArgsConstructor;
import org.abondar.experimental.articlemanager.aws.AwsProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

@Service
@AllArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public void uploadFile(String key, Path filePath) {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(awsProperties.getS3Bucket())
                .key(key)
                .build(), RequestBody.fromFile(filePath));
    }

}
