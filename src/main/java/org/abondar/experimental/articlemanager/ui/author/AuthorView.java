package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.abondar.experimental.articlemanager.ui.MainLayout;


@Route(value = "authors", layout = MainLayout.class)
@PageTitle("Article manager/Authors")
@Slf4j
public class AuthorView extends HorizontalLayout {

    public AuthorView(AuthorService authorService, AuthorAddUpdateForm authorAddUpdateForm) {
        var authorIdSpan = new Span("Author ID: N/A");
        var layout = new VerticalLayout();
        var author = new Author();
        var saveButton = new Button("Save", click -> {
            try {
                authorAddUpdateForm.getBinder().writeBean(author);
                var savedAuthor = authorService.save(author.getName(), author.getLastName(), author.getEmail());

                authorIdSpan.setText("Author ID: " + savedAuthor.getId());
            } catch (ValidationException e) {
                log.error(e.getMessage());
                Notification.show("Please correct the errors", 3000, Notification.Position.TOP_CENTER);
            }
        });
        layout.add(authorAddUpdateForm,saveButton);
        add(layout,authorIdSpan);

    }
}
