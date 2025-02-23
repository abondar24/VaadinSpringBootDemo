package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.aws.AwsProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private AwsProperties awsProperties;

    @InjectMocks
    private FileUploadService fileUploadService;

    @Test
    void uploadFileTest() throws IOException {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        var key = "test-key";

        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");

        fileUploadService.uploadFile(key, file);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFileExceptionTest() {
        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");

        assertThrows(RuntimeException.class, () -> fileUploadService.uploadFile("test-key", null));
    }

    @Test
    void deleteFileTest() {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        var key = "test-key";

        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");

        fileUploadService.deleteFile(key);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}
