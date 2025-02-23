package org.abondar.experimental.articlemanager.service;

import lombok.AllArgsConstructor;
import org.abondar.experimental.articlemanager.aws.AwsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@AllArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public void uploadFile(String key, MultipartFile file) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(awsProperties.getS3Bucket())
                .key(key)
                .contentType(file.getContentType())
                .build(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

    }

    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(awsProperties.getS3Bucket())
                .key(key)
                .build());
    }

}
