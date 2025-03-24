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
import java.util.Set;

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
        var mockAuthor = new Author("mockId", firstName, lastName, email, Set.of(), Set.of());

        when(authorRepository.save(any(Author.class))).thenReturn(mockAuthor);

        var res = authorService.save(firstName, lastName, email);
        assertNotNull(res.getId());
        assertEquals(firstName, res.getName());

    }


    @Test
    void updateConnectionsTest() {
        var author1 = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var author2 = new Author("testId2", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findById(author1.getId())).thenReturn(Optional.of(author1));
        when(authorRepository.findById(author2.getId())).thenReturn(Optional.of(author2));
        doNothing().when(authorRepository).createConnection(author1.getId(), author2.getId());

        authorService.connectAuthors(author1.getId(), author2.getId());

        verify(authorRepository, times(1)).findById(author1.getId());
        verify(authorRepository, times(1)).findById(author2.getId());
        verify(authorRepository, times(1)).createConnection(author1.getId(), author2.getId());
    }

    @Test
    void updateConnectionsAuthorNotFoundTest() {
        var author1 = new Author("testId1", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findById(author1.getId())).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.connectAuthors(author1.getId(), author1.getId()));
    }

    @Test
    void findConnectionsTest() {
        var author = new Author("testId1", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findConnectionsById(author.getId())).thenReturn(List.of(author));

        var res = authorService.findConnectionsById(author.getId());

        assertEquals(1, res.size());
    }

    @Test
    void deleteConnectionsTest() {
        var author1 = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        var author2 = new Author("testId2", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findById(author1.getId())).thenReturn(Optional.of(author1));
        when(authorRepository.findById(author2.getId())).thenReturn(Optional.of(author2));
        doNothing().when(authorRepository).removeConnection(author1.getId(), author2.getId());

        authorService.disconnectAuthors(author1.getId(), author2.getId());

        verify(authorRepository, times(1)).findById(author1.getId());
        verify(authorRepository, times(1)).findById(author2.getId());
        verify(authorRepository, times(1)).removeConnection(author1.getId(), author2.getId());
    }

    @Test
    void deleteAuthorTest() {
        var author = new Author("testId1", "test", "test", "test", Set.of(), Set.of());
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

        authorService.deleteAuthor(author.getId());

        verify(articleRepository, times(1)).removeMainAuthor(author.getId());
        verify(articleRepository, times(1)).removeCoAuthor(author.getId());
        verify(authorRepository, times(1)).findById(author.getId());
        verify(authorRepository, times(1)).deleteById(author.getId());
    }

    @Test
    void getUserByIdTest() {
        var author1 = new Author("testId1", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findById(author1.getId())).thenReturn(Optional.of(author1));

        var res = authorService.getAuthorById(author1.getId());
        assertEquals(author1, res);
    }

    @Test
    void getUserByIdTNotFoundTest() {

        when(authorRepository.findById("test")).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById("test"));
    }

    @Test
    void getAuthorsTest() {
        var author1 = new Author("testId1", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findAuthors(0,1)).thenReturn(List.of(author1));

        var res = authorService.getAuthors(0,1);
        assertEquals(1, res.size());
        assertEquals(author1, res.getFirst());
    }

    @Test
    void searchAuthorsTest(){
        var author1 = new Author("testId1", "test", "test", "test", Set.of(), Set.of());

        when(authorRepository.findByNameContainingIgnoreCase(author1.getName(), author1.getLastName()))
                .thenReturn(List.of(author1));

        var res = authorService.searchAuthors(author1.getName(), author1.getLastName());
        assertEquals(1, res.size());
        assertEquals(author1, res.getFirst());
    }

}
