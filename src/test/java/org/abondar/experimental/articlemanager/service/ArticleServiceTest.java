package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.exception.ArticleNotFoundException;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void saveAndUploadArticleTest() throws Exception {
        var mainAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var coAuthor = new Author("testId2", "test", "test", "test", List.of(), List.of());
        var coAuthors = List.of(coAuthor);
        var article = new Article("test", "test", "test", mainAuthor, coAuthors);
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());


        when(authorRepository.findById(any(String.class))).thenReturn(Optional.of(mainAuthor));
        when(authorRepository.findByIds(List.of(coAuthor.id()))).thenReturn(coAuthors);
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        var res = articleService.saveAndUploadArticle(article.title(), mainAuthor.id(), file, List.of(coAuthor.id()));

        verify(fileUploadService, times(1)).uploadFile(any(String.class), any(MultipartFile.class));
        assertNotNull(res);
        assertEquals(article.title(), res.title());

    }

    @Test
    void saveAndUploadArticleUploadErrorTest() throws Exception {
        var mainAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var coAuthor = new Author("testId2", "test", "test", "test", List.of(), List.of());
        var coAuthors = List.of(coAuthor);
        var article = new Article("test", "test", "test", mainAuthor, coAuthors);
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());


        when(authorRepository.findById(any(String.class))).thenReturn(Optional.of(mainAuthor));
        when(authorRepository.findByIds(List.of(coAuthor.id()))).thenReturn(coAuthors);
        doThrow(IOException.class).when(fileUploadService).uploadFile(any(String.class), any(MultipartFile.class));

        assertThrows(IOException.class, () -> articleService.saveAndUploadArticle(article.title(), mainAuthor.id(), file, List.of(coAuthor.id())));
        verify(fileUploadService, times(1)).uploadFile(any(String.class), any(MultipartFile.class));


    }

    @Test
    void setCoAuthorsTest() throws Exception {
        var mainAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var coAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());

        var coAutorIds = new ArrayList<String>();
        coAutorIds.add("coAuthorId");

        var coAuthors = List.of(coAuthor);

        var article = new Article("test", "test", "test", mainAuthor, new ArrayList<>());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(authorRepository.findByIds(coAutorIds)).thenReturn(coAuthors);

        articleService.setCoAuthors(coAutorIds, article.id());

        verify(authorRepository, times(1)).findByIds(coAutorIds);
        verify(articleRepository, times(1)).findById(any(String.class));
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void setCoAuthorsArticleNotFoundTest() {
        var mainAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var article = new Article("test", "test", "test", mainAuthor, List.of());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(ArticleNotFoundException.class, () -> articleService.setCoAuthors(List.of(), article.id()));

    }

    @Test
    void setCoAuthorsEmptyListTest() throws Exception {
        var mainAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var coAutorIds = List.of("tst");

        var article = new Article("test", "test", "test", mainAuthor, new ArrayList<>());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.of(article));
        when(authorRepository.findByIds(coAutorIds)).thenReturn(List.of());
        articleService.setCoAuthors(coAutorIds, article.id());

        verify(articleRepository, times(0)).save(any(Article.class));

    }

    @Test
    void getArticlesByAutorTest() {
        var mainAuthor = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var article = new Article("test", "test", "test", mainAuthor, new ArrayList<>());

        when(articleRepository.findArticlesByAuthor(mainAuthor.id())).thenReturn(List.of(article));

        var res = articleService.getArticlesByAuthor(mainAuthor.id());

        verify(articleRepository, times(1)).findArticlesByAuthor(mainAuthor.id());
        assertEquals(1, res.size());
    }

    @Test
    void deleteArticlesTest() throws Exception {
        var article = new Article("test", "test", "test", null, new ArrayList<>());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.of(article));

        articleService.deleteArticle(article.id());

        verify(fileUploadService, times(1)).deleteFile(any(String.class));
        verify(articleRepository, times(1)).delete(any(Article.class));
    }
}
