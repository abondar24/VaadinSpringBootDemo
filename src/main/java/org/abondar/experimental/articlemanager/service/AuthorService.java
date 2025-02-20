package org.abondar.experimental.articlemanager.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        var author = new Author(id, firstName, lastName, email, List.of(), List.of());

        log.info("Author saved with id {}", id);

        return authorRepository.save(author);
    }

    public void connectAuthors(String author1Id, String author2Id) {
        var author1 = authorRepository.findById(author1Id).orElseThrow(() -> throwException(author1Id));
        var author2 = authorRepository.findById(author2Id).orElseThrow(() -> throwException(author2Id));

        var updatedAuthor1 = new Author(author1.id(), author1.name(), author1.lastName(),
                author1.email(), author1.articles(), mergeConnections(author1, author2));

        var updatedAuthor2 = new Author(author2.id(), author2.name(), author2.lastName(),
                author2.email(), author2.articles(), mergeConnections(author2, author1));

        authorRepository.save(updatedAuthor1);
        authorRepository.save(updatedAuthor2);

    }

    public List<Author> findConnectionsById(String id) {
        return authorRepository.findAllById(List.of(id));
    }

    public List<Author> findArticlesById(String id) {
        return authorRepository.findAllById(List.of(id));
    }

    public void deleteAuthor(String id) {
        if (authorRepository.existsById(id)) {
            throw throwException(id);
        }

        articleRepository.removeMainAuthor(id);
        articleRepository.removeCoAuthor(id);
        authorRepository.removeRelationships(id);

        authorRepository.deleteById(id);
    }

    private List<Author> mergeConnections(Author author1, Author author2) {
        return new ArrayList<>(author1.connections()) {
            {
                add(author2);
            }
        };

    }

    private RuntimeException throwException(String authorId) {
        log.error("Author not found with id {}", authorId);
        return new AuthorNotFoundException("Author not found");
    }
}
