package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.service.ArticleService;
import org.abondar.experimental.articlemanager.ui.common.DeleteButton;
import org.abondar.experimental.articlemanager.ui.common.DownloadLink;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@SpringComponent
@UIScope
@Route(value = "/article/grid")
@Slf4j
public class ArticleGrid extends Grid<Article> {

    private static final String TITLE_COLUMN = "title";
    private static final String AUTHOR_COLUMN = "author";
    private final ArticleService articleService;



    public ArticleGrid(ArticleService articleService, AddUpdateArticleForm addUpdateArticleForm) {
        super(Article.class);

        this.articleService = articleService;

        addUpdateArticleForm.setArticleGrid(this);

        refresh();
        setSizeFull();
        setColumns(TITLE_COLUMN);
        getColumnByKey(TITLE_COLUMN).setResizable(true);

        addColumn(article -> article.getAuthor().getFullName(), AUTHOR_COLUMN).setHeader("Author");
        setSelectionMode(Grid.SelectionMode.SINGLE);

        CallbackDataProvider<Article, Void> dataProvider = DataProvider.fromCallbacks(
                query -> fetchArticles(query).stream(),
                query -> countArticles()
        );
        setDataProvider(dataProvider);

        addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(addUpdateArticleForm::editArticle));

        addComponentColumn(article -> {
            var link = new DownloadLink(article,articleService);

            var deleteBtn = new DeleteButton(click -> {
                articleService.deleteArticle(article.getId());
                Notification.show("Article deleted", 3000, Notification.Position.TOP_CENTER);
                log.info("Article deleted {}", article.getId());
                refresh();
            });

            var viewAuthorArticlesBtn = new Button(VaadinIcon.LIST_UL.create(), click ->
                   new AuthorArticlesDialog(article.getAuthor(),articleService).open()
            );
            viewAuthorArticlesBtn.setText("View author articles");


            var actionsLayout = new HorizontalLayout();
            actionsLayout.add(link, deleteBtn, viewAuthorArticlesBtn);
            actionsLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            return actionsLayout;
        });

        addAttachListener(event -> getDataProvider().refreshAll());
    }


    private List<Article> fetchArticles(Query<Article, Void> query) {
        var offset = query.getOffset();
        var limit = query.getLimit();
        return articleService.getArticles(offset, limit);
    }

    private int countArticles() {
        return (int) articleService.countArticles();
    }

    public void refresh() {
        getDataProvider().refreshAll();
    }

}
