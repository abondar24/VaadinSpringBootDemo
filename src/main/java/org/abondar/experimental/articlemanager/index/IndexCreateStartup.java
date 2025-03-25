package org.abondar.experimental.articlemanager.index;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IndexCreateStartup implements CommandLineRunner {

    private final IndexCreator indexCreator;

    public IndexCreateStartup(IndexCreator indexCreator) {
        this.indexCreator = indexCreator;
    }

    @Override
    public void run(String... args) throws Exception {
        indexCreator.createIndexIfNotExists();
    }
}