package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Author;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorServiceITest extends BaseIntegrationTest {


    @Test
    void saveAuthorTest() {
        var firstName = "John";
        var lastName = "Doe";
        var email = "john.doe@test.com";

        var author = authorService.save(firstName, lastName, email);
        assertNotNull(author.getId());
    }


    @Test
    void connectAuthorsTest() {
        var author1 = authorService.save("user1", "user1", "email1");
        var author2 = authorService.save("user2", "user2", "email2");

        authorService.connectAuthors(author1.getId(), author2.getId());

        var connections = authorService.findConnectionsById(author1.getId());
        assertFalse(connections.isEmpty());
        assertEquals(1, connections.size());
        assertEquals(author2.getId(), connections.getFirst().getId());
    }

    @Test
    void disconnectAuthorsTest() {
        var author1 = authorService.save("user1", "user1", "email1");
        var author2 = authorService.save("user2", "user2", "email2");

        authorService.connectAuthors(author1.getId(), author2.getId());

        authorService.disconnectAuthors(author1.getId(), author2.getId());

        var connections = authorService.findConnectionsById(author1.getId());
        assertTrue(connections.isEmpty());
    }

    @Test
    void updateAuthorTest() {
        var author = authorService.save("user1", "user1", "email1");
        author.setName("John");

        var res = authorService.updateAuthor(author);
        assertEquals(author.getName(), res.getName());
    }

    @Test
    void deleteAuthorTest() {
        var author1 = authorService.save("user1", "user1", "email1");

        authorService.deleteAuthor(author1.getId());
        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById(author1.getId()));
    }

    @Test
    void findAllAuthorsTest() {
        var author1 = authorService.save("user1", "user1", "email1");

        var res = authorService.getAuthors(0, 1);
        assertEquals(1, res.size());
        assertEquals(author1.getId(), res.getFirst().getId());
    }

    @Test
    void countAuthorsTest() {
        authorService.save("user1", "user1", "email1");
        var res = authorService.countAuthors();

        assertEquals(1, res);
    }

    @Test
    void connectionExistsTest() {
        var author1 = authorService.save("user1", "user1", "email1");
        var author2 = authorService.save("user2", "user2", "email2");

        authorService.connectAuthors(author1.getId(), author2.getId());

        var res = authorService.connectionExists(author1.getId(), author2.getId());
        assertTrue(res);
    }

    @Test
    void searchAuthorsByNameTest() {
        var author1 = authorService.save("user1", "user1", "email1");

        var res = authorService.searchAuthors(author1.getName());
        assertEquals(1, res.size());
        assertEquals(author1.getId(), res.getFirst().getId());
    }

    @Test
    void searchAuthorsByLastNameTest() {
        var author1 = authorService.save("user1", "user1", "email1");

        var res = authorService.searchAuthors(author1.getName()+" "+author1.getLastName());
        assertEquals(1, res.size());
        assertEquals(author1.getId(), res.getFirst().getId());
    }
}
