package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.aws.AwsProperties;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
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
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Tag("integration")
public class FileUploadServiceITest {

    @Container
    private static final GenericContainer<?> localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3)
            .withExposedPorts(4566);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private S3Client s3Client;


    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.localstack.endpoint", () -> "http://" + localStack.getHost() + ":" + localStack.getFirstMappedPort());
    }

    @BeforeAll
    static void init() {
        localStack.start();

    }

    @BeforeEach
    void createBucket(){
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket("articles")
                .build());
    }

    @Test
    void uploadFileTest() throws IOException {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        var key = "test-key";

        fileUploadService.uploadFile(key, file);

        var s3Object = s3Client.getObject(GetObjectRequest.builder()
                .bucket("articles")
                .key(key)
                .build());

        assertNotNull(s3Object);
        assertEquals(file.getContentType(),s3Object.response().contentType());
    }

    @Test
    void deleteFileTest() throws IOException {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        var key = "test-key";

        fileUploadService.uploadFile(key, file);

        fileUploadService.deleteFile(key);

        assertThrows(NoSuchKeyException.class, () ->  s3Client.getObject(GetObjectRequest.builder()
                .bucket("articles")
                .key(key)
                .build()));

    }


}
