package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.abondar.experimental.articlemanager.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ArticleServiceITest extends BaseIntegrationTest {


    private Author author;


    @BeforeEach
    void createAuthor() {
        author = authorService.save("John", "Doe", "john.doe@test.com");
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
