package org.abondar.experimental.articlemanager.service;

import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Author;
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
        assertNotNull(author.id());
    }


    @Test
    void connectAuthorsTest() {
        var author1 = authorService.save("user1", "user1", "email1");
        var author2 = authorService.save("user2", "user2", "email2");

        authorService.connectAuthors(author1.id(), author2.id());

        var connections = authorService.findConnectionsById(author1.id());
        assertFalse(connections.isEmpty());
        assertEquals(1, connections.size());
        assertEquals(author2.id(), connections.getFirst().id());
    }

    @Test
    void updateAuthorTest() {
        var author1 = authorService.save("user1", "user1", "email1");
        var update = new Author(author1.id(), "newUser", author1.lastName(), author1.email(),
                author1.articles(), author1.connections());

        var res = authorService.updateAuthor(update);
        assertEquals(update.name(), res.name());
    }

    @Test
    void deleteAuthorTest() {
        var author1 = authorService.save("user1", "user1", "email1");

        authorService.deleteAuthor(author1.id());
        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById(author1.id()));
    }
}
