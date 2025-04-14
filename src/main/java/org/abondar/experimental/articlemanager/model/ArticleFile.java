package org.abondar.experimental.articlemanager.model;

import java.io.InputStream;

public record ArticleFile(
        InputStream file,
        long length,
        String contentType,
        String filename
) {
}
