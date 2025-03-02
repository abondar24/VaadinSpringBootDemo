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
        binder = new Binder<>(Author.class);

        var firstName = new TextField();
        addFormItem(firstName,"First Name");
        binder.bind(firstName, Author::getName,
                Author::setName);

        var lastName = new TextField();
        addFormItem(lastName,"Last Name");
        binder.bind(lastName, Author::getLastName,
                Author::setLastName);

        var email = new TextField();
        addFormItem(email,"Email");
        binder.bind(email, Author::getEmail,
                Author::setEmail);

    }

}
