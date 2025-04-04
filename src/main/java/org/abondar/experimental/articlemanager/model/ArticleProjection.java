package org.abondar.experimental.articlemanager.model;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public interface ArticleProjection {
    String getId();

    String getTitle();

    String getArticleKey();
}
