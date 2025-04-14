package org.abondar.experimental.articlemanager.service;

import lombok.AllArgsConstructor;
import org.abondar.experimental.articlemanager.aws.AwsProperties;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Map;

@Service
@AllArgsConstructor
public class S3FileService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public void uploadFile(String key, ArticleFile articleFile) {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(awsProperties.getS3Bucket())
                .key(key)
                .contentType("multipart/form-data")
                .metadata(Map.of(
                        "original-filename", articleFile.filename()
                ))
                .build(), RequestBody.fromInputStream(articleFile.file(), articleFile.length()));
    }

    public InputStream downloadFile(String key) {
        return s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(awsProperties.getS3Bucket())
                        .key(key)
                        .build()
        );
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(awsProperties.getS3Bucket())
                .key(key)
                .build());
    }

}
