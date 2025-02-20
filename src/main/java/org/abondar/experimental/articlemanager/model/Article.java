package org.abondar.experimental.articlemanager.model;

import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

public record Article(

        @Id String id,

        String title, String fileUrl,

        @Relationship(type = "AUTHOR") Author author,

        @Relationship(type = "CO_AUTHOR") List<Author> coAuthors) {

}
