package org.abondar.experimental.articlemanager.repository;

import org.abondar.experimental.articlemanager.model.Article;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends Neo4jRepository<Article,String> {

    @Query("MATCH (a:Article)-[r:AUTHOR]->(author:Author {id: $authorId}) DELETE r")
    void removeMainAuthor(@Param("authorId") String authorId);

    @Query("MATCH (a:Article)-[r:CO_AUTHOR]->(author:Author {id: $authorId}) DELETE r")
    void removeCoAuthor(@Param("authorId") String authorId);

    @Query("MATCH (a:Article) WHERE a.mainAuthor.id = $authorId")
    List<Article> findArticlesByAuthor(String authorId);

    @Query("MATCH (a:Article {id: $id}) DETACH DELETE a")
    void deleteById(@Param("id") String id);
}
