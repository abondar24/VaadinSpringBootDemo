package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import org.abondar.experimental.articlemanager.model.Author;

@SpringComponent
@UIScope
@Route(value = "author/add")
@PageTitle("Article manager/Manage author")
@Getter
public class AuthorAddUpdateForm extends FormLayout {

    private final Binder<Author> binder;

    public AuthorAddUpdateForm() {
        this.binder = new Binder<>(Author.class);

        var firstName = new TextField();
        firstName.setRequiredIndicatorVisible(true);
        firstName.setRequired(true);
        addFormItem(firstName, "First Name");
        binder.bind(firstName, Author::getName,
                Author::setName);

        var lastName = new TextField();
        lastName.setRequiredIndicatorVisible(true);
        lastName.setRequired(true);
        addFormItem(lastName, "Last Name");
        binder.bind(lastName, Author::getLastName,
                Author::setLastName);

        var email = new TextField();
        email.setRequiredIndicatorVisible(true);
        email.setRequired(true);
        addFormItem(email, "Email");
        binder.bind(email, Author::getEmail,
                Author::setEmail);

    }

    public void setAuthor(Author author) {
        binder.setBean(author);
    }

}
