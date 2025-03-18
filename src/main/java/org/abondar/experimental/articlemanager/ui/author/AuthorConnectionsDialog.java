package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;

import java.util.List;


@Slf4j
public class AuthorConnectionsDialog extends Dialog {


    public AuthorConnectionsDialog(List<Author> authors) {
        setHeaderTitle("Author Connections");

        if (authors.isEmpty()) {
            var span = new Span("There are no authors connected");
            add(span);
        }

        var closeBtn = new Button(new Icon("lumo", "cross"),
                (e) -> this.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getHeader().add(closeBtn);

        VirtualList<Author> authorsList = new VirtualList<>();
        authorsList.setItems(authors);
        authorsList.setRenderer(getRenderer());
        add(authorsList);

    }

    private ComponentRenderer<Component, Author> getRenderer() {
        return new ComponentRenderer<>(author -> {
            var cardLayout = new HorizontalLayout();
            cardLayout.setMargin(true);

            var fullName = author.getName() +" "+ author.getLastName();
            var avatar = new Avatar(fullName);
            avatar.setHeight("64px");
            avatar.setWidth("64px");

            var infoLayout = new VerticalLayout();
            infoLayout.setSpacing(false);
            infoLayout.setPadding(false);
            infoLayout.getElement()
                    .appendChild(ElementFactory.createStrong(fullName));
            infoLayout.add(new Div(new Text(author.getEmail())));

            cardLayout.add(avatar, infoLayout);
            return cardLayout;
        });
    }
}
