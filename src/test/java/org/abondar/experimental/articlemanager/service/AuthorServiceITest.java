package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Tag("integration")
public class AuthorServiceITest {


    @Container
    private static final GenericContainer<?> neo4j = new Neo4jContainer<>("neo4j:latest")
            .withExposedPorts(7687)
            .withEnv("NEO4J_AUTH", "none");

    @Autowired
    private AuthorService authorService;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", () -> "bolt://" + neo4j.getHost() + ":" + neo4j.getFirstMappedPort());
    }

    @BeforeAll
    static void init() {
        neo4j.start();
    }

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
        assertEquals(author1, res.getFirst());
    }

    @Test
    void countAuthorsTest() {
        authorService.save("user1", "user1", "email1");
        var res = authorService.countAuthors();

        assertEquals(1, res);
    }
}
