package org.abondar.experimental.articlemanager.repository;

import org.abondar.experimental.articlemanager.model.Article;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends Neo4jRepository<Article,String> {

    @Query("MATCH (a:Article) WHERE a.mainAuthor.id = $authorId SET a.mainAuthor = NULL")
    void removeMainAuthor(String authorId);

    @Query("MATCH (a:Article)-[r:CO_AUTHOR]->(author:Author) WHERE author.id = $authorId DELETE r")
    void removeCoAuthor(String authorId);
}
