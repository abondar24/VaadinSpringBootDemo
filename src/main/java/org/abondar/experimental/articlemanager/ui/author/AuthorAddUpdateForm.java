package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;

@SpringComponent
@UIScope
@Route(value = "author/add")
@PageTitle("Article manager/Manage author")
@Getter
@Slf4j
public class AuthorAddUpdateForm extends FormLayout {

    private final Binder<Author> binder;
    private final AuthorService authorService;

    @Setter
    private AuthorGrid authorGrid;

    public AuthorAddUpdateForm(AuthorService authorService) {
        this.authorService = authorService;
        this.binder = new Binder<>(Author.class);

        var firstName = new TextField();
        firstName.setRequiredIndicatorVisible(true);
        firstName.setRequired(true);
        binder.bind(firstName, Author::getName,
                Author::setName);
        addFormItem(firstName, "First Name");

        var lastName = new TextField();
        lastName.setRequiredIndicatorVisible(true);
        lastName.setRequired(true);
         binder.bind(lastName, Author::getLastName,
                Author::setLastName);
        addFormItem(lastName, "Last Name");

        var email = new TextField();
        email.setRequiredIndicatorVisible(true);
        email.setRequired(true);
        binder.bind(email, Author::getEmail,
                Author::setEmail);
        addFormItem(email, "Email");

        var saveButton = new Button("Save", click -> saveAuthor());
        var cancelButton = new Button("Cancel", click -> clearForm());

        var buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        add(buttonLayout);

        clearForm();
    }

    private void saveAuthor() {
        {
            try {
                var authorToSave = binder.getBean();
                binder.writeBeanIfValid(authorToSave);

                boolean isNew = authorToSave.getId() == null;
                Author savedAuthor;

                if (isNew) {
                    savedAuthor = authorService.save(authorToSave.getName(), authorToSave.getLastName(),
                            authorToSave.getEmail());
                    log.info("Saved author {}", savedAuthor.getId());
                    Notification.show("Author saved successfully", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    savedAuthor = authorService.updateAuthor(authorToSave);
                    log.info("Updated author {}", savedAuthor.getId());
                    Notification.show("Author updated successfully", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }

                if (authorGrid != null) {
                    authorGrid.refresh();

                }

                clearForm();
            } catch (Exception e) {
                log.error("Error saving/updating author", e);
                Notification.show("An error occurred: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }


    public void clearForm() {
        binder.setBean(new Author());
    }

    public void setAuthorToEdit(Author author) {
        binder.setBean(author);
    }

}
