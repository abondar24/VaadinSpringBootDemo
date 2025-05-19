package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import static org.abondar.experimental.articlemanager.service.ContainerConfig.LOCAL_STACK;
import static org.abondar.experimental.articlemanager.service.ContainerConfig.NEO_4_J;

@SpringBootTest
@Tag("integration")
@Import(ContainerConfig.class)
public class BaseIntegrationTest {

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", () -> "bolt://" + NEO_4_J.getHost() + ":" + NEO_4_J.getFirstMappedPort());
        registry.add("aws.localstack.endpoint", () -> "http://" + LOCAL_STACK.getHost() + ":" + LOCAL_STACK.getFirstMappedPort());
    }

    private static final String TEST_BUCKET = "articles";

    @Autowired
    protected ArticleService articleService;

    @Autowired
    protected AuthorService authorService;

    @Autowired
    protected S3Client s3Client;

    @Autowired
    protected Neo4jTemplate neo4jTemplate;


    @BeforeEach
    void setup(){
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(TEST_BUCKET)
                .build());
    }

    @AfterEach
    void tearDown(){
        neo4jTemplate.deleteAll(Author.class);
        neo4jTemplate.deleteAll(Article.class);

        var objectsToDelete =  s3Client.listObjectsV2(builder -> builder.bucket(TEST_BUCKET))
                .contents()
                .stream()
                .map(s3Object -> ObjectIdentifier.builder()
                        .key(s3Object.key())
                        .build())
                .toList();

        if (!objectsToDelete.isEmpty()){
            s3Client.deleteObjects(DeleteObjectsRequest -> DeleteObjectsRequest
                    .delete(Delete.builder().objects(objectsToDelete).build())
                    .bucket(TEST_BUCKET));
        }

        s3Client.deleteBucket(DeleteBucketRequest -> DeleteBucketRequest.bucket(TEST_BUCKET));
    }
}
