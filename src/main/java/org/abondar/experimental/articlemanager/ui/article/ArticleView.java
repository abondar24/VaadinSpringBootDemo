package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.abondar.experimental.articlemanager.ui.MainLayout;

@Route(value = "articles", layout = MainLayout.class)
@PageTitle("Article manager/Articles")
@Slf4j
public class ArticleView extends HorizontalLayout {

    private final AuthorService authorService;

    private final AddUpdateArticleForm addUpdateArticleForm;


    public ArticleView(AuthorService authorService, AddUpdateArticleForm addUpdateArticleForm) {
        this.authorService = authorService;

        this.addUpdateArticleForm = addUpdateArticleForm;

        var formLayout = new VerticalLayout();
        formLayout.add(addUpdateArticleForm);


        add(formLayout);
    }
}
