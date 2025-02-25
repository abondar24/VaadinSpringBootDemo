package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private AuthorService authorService;


    @Test
    void saveAuthorTest() {
        var firstName = "John";
        var lastName = "Doe";
        var email = "john.doe@gmail.com";
        var mockAuthor = new Author("mockId", firstName, lastName, email, List.of(), List.of());

        when(authorRepository.save(any(Author.class))).thenReturn(mockAuthor);

        var res = authorService.save(firstName, lastName, email);
        assertNotNull(res.id());
        assertEquals(firstName, res.name());

    }


    @Test
    void updateConnectionsTest() {
        var author1 = new Author("testId1", "test", "test", "test", List.of(), List.of());
        var author2 = new Author("testId2", "test", "test", "test", List.of(), List.of());

        when(authorRepository.findById(author1.id())).thenReturn(Optional.of(author1));
        when(authorRepository.findById(author2.id())).thenReturn(Optional.of(author2));
        doNothing().when(authorRepository).createConnection(author1.id(), author2.id());

        authorService.connectAuthors(author1.id(), author2.id());

        verify(authorRepository, times(1)).findById(author1.id());
        verify(authorRepository, times(1)).findById(author2.id());
        verify(authorRepository, times(1)).createConnection(author1.id(), author2.id());
    }

    @Test
    void updateConnectionsAuthorNotFoundTest() {
        var author1 = new Author("testId1", "test", "test", "test", List.of(), List.of());

        when(authorRepository.findById(author1.id())).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.connectAuthors(author1.id(), author1.id()));
    }

    @Test
    void findConnectionsTest() {
        var author = new Author("testId1", "test", "test", "test", List.of(), List.of());

        when(authorRepository.findConnectionsById(author.id())).thenReturn(List.of(author));

        var res = authorService.findConnectionsById(author.id());

        assertEquals(1, res.size());
    }

    @Test
    void deleteAuthorTest() {
        var author = new Author("testId1", "test", "test", "test", List.of(), List.of());
        when(authorRepository.findById(author.id())).thenReturn(Optional.of(author));

        authorService.deleteAuthor(author.id());

        verify(articleRepository, times(1)).removeMainAuthor(author.id());
        verify(articleRepository, times(1)).removeCoAuthor(author.id());
        verify(authorRepository, times(1)).findById(author.id());
        verify(authorRepository, times(1)).deleteById(author.id());
    }

    @Test
    void getUserByIdTest() {
        var author1 = new Author("testId1", "test", "test", "test", List.of(), List.of());

        when(authorRepository.findById(author1.id())).thenReturn(Optional.of(author1));

        var res = authorService.getAuthorById(author1.id());
        assertEquals(author1, res);
    }

    @Test
    void getUserByIdTNotFoundest() {

        when(authorRepository.findById("test")).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById("test"));
    }

}
