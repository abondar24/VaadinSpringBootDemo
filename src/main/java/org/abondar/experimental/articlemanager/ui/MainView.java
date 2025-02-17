package org.abondar.experimental.articlemanager.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends AppLayout {

    public MainView() {
        addToNavbar(new Span("Article Manager"));
    }
}
