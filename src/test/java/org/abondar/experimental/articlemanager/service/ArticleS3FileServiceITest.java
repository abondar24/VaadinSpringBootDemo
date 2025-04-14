package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Tag("integration")
public class ArticleS3FileServiceITest {

    @Container
    private static final GenericContainer<?> localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3)
            .withExposedPorts(4566);

    @Autowired
    private S3FileService s3FileService;

    @Autowired
    private S3Client s3Client;


    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.localstack.endpoint", () -> "http://" + localStack.getHost() + ":" + localStack.getFirstMappedPort());
    }

    @BeforeEach
    void createBucket() {
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket("articles")
                .build());
    }

    @Test
    void uploadFileTest() throws IOException {
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());
        var key = "test-key";

        s3FileService.uploadFile(key, new ArticleFile(file.getInputStream(), file.getSize(),
                "test", file.getOriginalFilename()));

        var s3Object = s3Client.getObject(GetObjectRequest.builder()
                .bucket("articles")
                .key(key)
                .build());

        assertNotNull(s3Object);
        assertEquals(file.getContentType(), s3Object.response().contentType());
    }

    @Test
    void downloadFileTest() throws IOException {
        var fileContent = "test";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", fileContent.getBytes());
        var key = "test-key";

        s3FileService.uploadFile(key, new ArticleFile(file.getInputStream(), file.getSize(),
                "test", file.getOriginalFilename()));

        var fileStream = s3FileService.downloadFile(key);

        assertNotNull(fileStream);

        var downloadedContent = new String(fileStream.readAllBytes());
        assertEquals(fileContent, downloadedContent);
    }

    @Test
    void deleteFileTest() throws IOException {
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());
        var key = "test-key";

        s3FileService.uploadFile(key, new ArticleFile(file.getInputStream(), file.getSize(),
                "test", file.getOriginalFilename()));

        s3FileService.deleteFile(key);

        assertThrows(NoSuchKeyException.class, () -> s3Client.getObject(GetObjectRequest.builder()
                .bucket("articles")
                .key(key)
                .build()));

    }


}
