package org.abondar.experimental.articlemanager.index;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexCreator {

    private final Driver driver;


    @Autowired
    public IndexCreator(Driver driver) {
        this.driver = driver;
    }

    public void createIndexIfNotExists() {
        try (Session session = driver.session()) {
            String query = "CREATE FULLTEXT INDEX authorIndex IF NOT EXISTS FOR (n:Author) ON EACH [n.name, n.lastName]";
            session.run(query);
        }
    }


}
