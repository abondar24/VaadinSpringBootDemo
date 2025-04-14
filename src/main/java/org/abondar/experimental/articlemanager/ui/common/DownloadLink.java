package org.abondar.experimental.articlemanager.ui.common;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.service.ArticleService;

import java.io.InputStream;


public class DownloadLink extends Anchor {


    public DownloadLink(Article article, ArticleService articleService) {
        var articleFile = article.getArticleKey().split("/")[2];
        var resource = new StreamResource(articleFile, () -> articleService.getArticleFile(article.getArticleKey()));

        resource.setContentType("application/octet-stream");
        resource.setHeader("Content-Disposition", "attachment; filename=\"" + articleFile + "\"");

        this.setHref(resource);
        this.setText("Download");
        this.setTarget("_blank");
    }

}
