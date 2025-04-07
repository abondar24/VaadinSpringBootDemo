package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.ui.MainLayout;

@Route(value = "articles", layout = MainLayout.class)
@PageTitle("Article manager/Articles")
@Slf4j
public class ArticleView extends VerticalLayout {


    public ArticleView(AddUpdateArticleForm addUpdateArticleForm, ArticleGrid articleGrid) {
        setSizeFull();
        add(addUpdateArticleForm);
        add(articleGrid);
    }
}
