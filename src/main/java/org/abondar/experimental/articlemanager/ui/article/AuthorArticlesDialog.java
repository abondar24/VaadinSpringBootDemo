package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.Author;

import java.util.List;


@Route(value = "article/list")
@Slf4j
public class AuthorArticlesDialog extends Dialog {

    public AuthorArticlesDialog(List<Article> articles, Author author) {
        setHeaderTitle("Articles of: " + author.getFullName());

        if (articles.isEmpty()) {
            var span = new Span("No articles available for selected author");
            add(span);
        } else {
            VirtualList<Article> articleList = new VirtualList<>();
            articleList.setItems(articles);
            articleList.setRenderer(createArticleRenderer());
            add(articleList);
        }

        //TODO add file downloading for each article in seprate component

        var close = new Button("Close", e -> close());
        getFooter().add(close);
    }

    private ComponentRenderer<Component, Article> createArticleRenderer() {
        return new ComponentRenderer<>(
                article -> {
                    var articleLayout = new VerticalLayout();
                    articleLayout.setSpacing(false);
                    articleLayout.setMargin(false);

                    articleLayout.getElement().appendChild(
                            ElementFactory.createStrong(article.getTitle())
                    );

                    var coAuthorLayout = new VerticalLayout();
                    coAuthorLayout.add(new Span("Co-authors:"));
                    coAuthorLayout.setSpacing(false);
                    coAuthorLayout.setPadding(false);

                    if (article.getCoAuthors() != null) {
                        article.getCoAuthors().forEach(coAuthor ->
                                coAuthorLayout.add(new Div(new Text(coAuthor.getFullName())))
                        );


                        articleLayout.add(coAuthorLayout);
                    }


                    return articleLayout;
                }
        );
    }
}
