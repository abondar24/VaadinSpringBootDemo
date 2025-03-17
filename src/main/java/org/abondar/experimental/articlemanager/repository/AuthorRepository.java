package org.abondar.experimental.articlemanager.repository;

import org.abondar.experimental.articlemanager.model.Author;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends Neo4jRepository<Author,String> {

    @Query("MATCH (a:Author {id: $author1Id}), (b:Author {id: $author2Id}) " +
            "MERGE (a)-[:KNOWS]->(b) MERGE (b)-[:KNOWS]->(a)")
    void createConnection(@Param("author1Id") String author1Id,
                          @Param("author2Id") String author2Id);

    @Query("MATCH (a:Author)-[:KNOWS]->(b:Author) WHERE a.id = $id RETURN b")
    List<Author> findConnectionsById(String id);

    @Query("MATCH (a:Author)-[r:KNOWS]-(b:Author) WHERE a.id = $id DELETE r")
    void removeRelationships(String id);


    @Query("MATCH (a:Author) WHERE a.id IN $ids RETURN a")
    List<Author> findByIds(List<String> ids);

    @Query("MATCH (a:Author) RETURN a SKIP $offset LIMIT $limit")
    List<Author> findAuthors(@Param("offset") int offset, @Param("limit") int limit);
}
