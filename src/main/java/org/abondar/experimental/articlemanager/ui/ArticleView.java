package org.abondar.experimental.articlemanager.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "articles", layout = MainLayout.class)
@PageTitle("Article manager/Articles")
public class ArticleView extends HorizontalLayout {
    public ArticleView() {

        var btn = new Button("Add Article");
        add(btn);
    }
}
