package org.abondar.experimental.articlemanager.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
public record Author(

        @Id String id,

        String name, String lastName, String email,

        @Relationship(type = "WROTE") List<Article> articles,

        @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING) List<Author> connections) {
}
