package org.abondar.experimental.articlemanager.ui.common;

import com.vaadin.flow.component.html.Anchor;
import org.abondar.experimental.articlemanager.model.Article;


public class DownloadLink extends Anchor {
    //TODO fix save in readableformat
    public DownloadLink(Article article, String awsEndpoint, String bucketName ) {
        super(awsEndpoint + "/" + bucketName + "/" + article.getArticleKey(), article.getTitle());
        setTarget("_blank");
    }

}
