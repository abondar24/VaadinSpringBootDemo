package org.abondar.experimental.articlemanager.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final ArticleRepository articleRepository;

    public Author save(String firstName, String lastName, String email) {
        var id = UUID.randomUUID().toString();
        var author = new Author(id, firstName, lastName, email, Set.of(), Set.of());

        log.info("Author saved with id {}", id);

        return authorRepository.save(author);
    }

    //TODO handle authors already connected
    public void connectAuthors(String author1Id, String author2Id) {
        getAuthorById(author1Id);
        getAuthorById(author2Id);

        authorRepository.createConnection(author1Id, author2Id);
    }

    //TODO handle authors already disconnected
    public void disconnectAuthors(String author1Id, String author2Id) {
        getAuthorById(author1Id);
        getAuthorById(author2Id);

        authorRepository.removeConnection(author1Id, author2Id);
    }

    //TODO: add to ui
    public Author updateAuthor(Author author) {
        getAuthorById(author.getId());

        return authorRepository.save(author);
    }

    public List<Author> findConnectionsById(String id) {
        return authorRepository.findConnectionsById(id);
    }

    public void deleteAuthor(String id) {
        getAuthorById(id);

        articleRepository.removeMainAuthor(id);
        articleRepository.removeCoAuthor(id);
        authorRepository.removeRelationships(id);

        authorRepository.deleteById(id);
    }

    public Author getAuthorById(String id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
    }

    public List<Author> getAuthors(int offset, int limit) {
        return authorRepository.findAuthors(offset, limit);
    }

    public long countAuthors() {
        return authorRepository.count();
    }

}
