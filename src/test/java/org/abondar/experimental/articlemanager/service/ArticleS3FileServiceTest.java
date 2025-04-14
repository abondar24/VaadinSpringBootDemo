package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.aws.AwsProperties;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleS3FileServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private AwsProperties awsProperties;

    @InjectMocks
    private S3FileService s3FileService;

    @Test
    void uploadFileTest() throws IOException {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        var key = "test-key";

        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");

        s3FileService.uploadFile(key, new ArticleFile(file.getInputStream(),file.getSize(),"test",file.getOriginalFilename()));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFileExceptionTest() {
        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");

        assertThrows(RuntimeException.class, () -> s3FileService.uploadFile("test-key", null));
    }

    @Test
    void downloadFileTest() throws IOException {
        var key = "test-key";

        var is = new ByteArrayInputStream("test".getBytes());
        var responseInputStream = new ResponseInputStream<>(GetObjectResponse.builder()
                .build(), AbortableInputStream.create(is));


        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

        var res = s3FileService.downloadFile(key);
        assertNotNull(res);
        assertEquals("test", new String(res.readAllBytes()));
    }

    @Test
    void deleteFileTest() {
        var key = "test-key";

        when(awsProperties.getS3Bucket()).thenReturn("test-bucket");

        s3FileService.deleteFile(key);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}
