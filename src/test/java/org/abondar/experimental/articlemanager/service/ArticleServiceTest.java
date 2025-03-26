package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.exception.ArticleNotFoundException;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        var mainAuthor = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var coAuthor = new Author("testId2", "test", "test", "test", Set.of(), Set.of());
        var coAuthors = List.of(coAuthor);
        var article = new Article("test", "test", "test", mainAuthor, coAuthors);
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());


        when(authorRepository.findById(any(String.class))).thenReturn(Optional.of(mainAuthor));
        when(authorRepository.findByIds(List.of(coAuthor.getId()))).thenReturn(coAuthors);
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        var res = articleService.saveAndUploadArticle(article.getTitle(), mainAuthor.getId(),
                new ArticleFile(file.getInputStream(),file.getSize()), List.of(coAuthor.getId()));

        verify(fileUploadService, times(1)).uploadFile(any(String.class), any(ArticleFile.class));
        assertNotNull(res);
        assertEquals(article.getTitle(), res.getTitle());

    }

    @Test
    void saveAndUploadArticleUploadErrorTest() throws Exception {
        var mainAuthor = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var coAuthor = new Author("testId2", "test", "test", "test", Set.of(), Set.of());
        var coAuthors = List.of(coAuthor);
        var article = new Article("test", "test", "test", mainAuthor, coAuthors);
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());


        when(authorRepository.findById(any(String.class))).thenReturn(Optional.of(mainAuthor));
        when(authorRepository.findByIds(List.of(coAuthor.getId()))).thenReturn(coAuthors);
        doThrow(IOException.class).when(fileUploadService).uploadFile(any(String.class), any(ArticleFile.class));

        assertThrows(IOException.class, () -> articleService.saveAndUploadArticle(article.getTitle(), mainAuthor.getId(),
                new ArticleFile(file.getInputStream(),file.getSize()), List.of(coAuthor.getId())));
        verify(fileUploadService, times(1)).uploadFile(any(String.class), any(ArticleFile.class));


    }

    @Test
    void updateArticleTest() throws Exception {
        var mainAuthor = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var coAuthor = new Author("testId1", "test", "test", "test", Set.of(), Set.of());

        var coAutorIds = new ArrayList<String>();
        coAutorIds.add("coAuthorId");

        var coAuthors = List.of(coAuthor);

        var article = new Article("test", "test", "test", mainAuthor, new ArrayList<>());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.of(article));
        when(authorRepository.findByIds(coAutorIds)).thenReturn(coAuthors);
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.updateArticle(article, null, coAutorIds);

        verify(authorRepository, times(1)).findByIds(coAutorIds);
        verify(articleRepository, times(1)).findById(any(String.class));
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void setCoAuthorsArticleNotFoundTest() {
        var mainAuthor = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var article = new Article("test", "test", "test", mainAuthor, List.of());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(ArticleNotFoundException.class, () -> articleService.updateArticle(article, null, List.of()));

    }

    @Test
    void getArticlesByAutorTest() {
        var mainAuthor = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var article = new Article("test", "test", "test", mainAuthor, new ArrayList<>());

        when(articleRepository.findArticlesByAuthor(mainAuthor.getId())).thenReturn(List.of(article));

        var res = articleService.getArticlesByAuthor(mainAuthor.getId());

        verify(articleRepository, times(1)).findArticlesByAuthor(mainAuthor.getId());
        assertEquals(1, res.size());
    }

    @Test
    void deleteArticlesTest() throws Exception {
        var article = new Article("test", "test", "test", null, new ArrayList<>());

        when(articleRepository.findById(any(String.class))).thenReturn(Optional.of(article));

        articleService.deleteArticle(article.getId());

        verify(fileUploadService, times(1)).deleteFile(any(String.class));
        verify(articleRepository, times(1)).delete(any(Article.class));
    }
}
