package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.abondar.experimental.articlemanager.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Tag("integration")
public class ArticleServiceITest {

    @Container
    private static final GenericContainer<?> neo4j = new Neo4jContainer<>("neo4j:latest")
            .withExposedPorts(7687)
            .withEnv("NEO4J_AUTH", "none");

    @Container
    private static final GenericContainer<?> localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3)
            .withExposedPorts(4566);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    private Author author;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", () -> "bolt://localhost:" + neo4j.getMappedPort(7687));
        registry.add("aws.localstack.endpoint", () -> "http://" + localStack.getHost() + ":" + localStack.getFirstMappedPort());
    }

    @BeforeEach
    void createBucket() {
        author = authorService.save("John", "Doe", "john.doe@test.com");

        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket("articles")
                .build());

        neo4jTemplate.deleteAll(Article.class);
    }


    @Test
    void saveAndUploadArticleTest() throws Exception {
        var title = "test Title";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());

        var article = articleService.saveAndUploadArticle(title, author.getId(),
                new ArticleFile(file.getInputStream(), file.getSize(), "test", file.getOriginalFilename()), List.of());
        assertNotNull(article.getId());
    }

    @Test
    void getArticlesByAuthorTestById() throws Exception {
        var title = "test Title";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());

        var coAuthor = authorService.save("James", "Din", "james.din@test.com");

        var article = articleService.saveAndUploadArticle(title, author.getId(),
                new ArticleFile(file.getInputStream(), file.getSize(), "test", file.getOriginalFilename()),
                List.of(coAuthor.getId()));

        var res = articleService.getArticlesByAuthor(author.getId());
        assertFalse(res.isEmpty());
        assertEquals(article.getId(), res.getFirst().getId());
        assertEquals(coAuthor.getId(), res.getFirst().getCoAuthors().getFirst().getId());
    }


    @Test
    void updateArticleTest() throws Exception {
        var title = "test Title";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());
        var article = articleService.saveAndUploadArticle(title, author.getId(),
                new ArticleFile(file.getInputStream(), file.getSize(), "test", file.getOriginalFilename()), List.of());

        var coAuthor = authorService.save("John", "Test", "john.test@test.com");
        var res = articleService.updateArticle(article, null, List.of(coAuthor.getId()));

        assertFalse(res.getCoAuthors().isEmpty());
        assertEquals(coAuthor.getId(), res.getCoAuthors().getFirst().getId());

        articleService.deleteArticle(article.getId());
    }

    @Test
    void deleteArticleTest() throws Exception {
        var title = "test Title";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());
        var article = articleService.saveAndUploadArticle(title, author.getId(),
                new ArticleFile(file.getInputStream(), file.getSize(), "test", file.getOriginalFilename()), List.of());

        articleService.deleteArticle(article.getId());

        var res = articleService.getArticlesByAuthor(author.getId());
        assertTrue(res.isEmpty());
    }

    @Test
    void getArticlesByIdTest() throws Exception {
        var title = "test Title";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());
        var article = articleService.saveAndUploadArticle(title, author.getId(),
                new ArticleFile(file.getInputStream(), file.getSize(), "test", file.getOriginalFilename()), List.of());

        var res = articleService.getArticles(0, 1);
        assertFalse(res.isEmpty());
        assertEquals(article.getId(), res.getFirst().getId());
        assertNotNull(res.getFirst().getAuthor());
    }

    @Test
    void countArticlesTest() throws Exception {
        var title = "test Title";
        var file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test".getBytes());
        articleService.saveAndUploadArticle(title, author.getId(),
                new ArticleFile(file.getInputStream(), file.getSize(), "test", file.getOriginalFilename()), List.of());

        var res = articleService.countArticles();
        assertEquals(1, res);
    }
}
