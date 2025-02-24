package org.abondar.experimental.articlemanager.model;


import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

public record Article(

        @Id String id,

        String title, String articleKey,

        @Relationship(type = "AUTHOR") Author author,

        @Relationship(type = "CO_AUTHOR") List<Author> coAuthors) {

}
