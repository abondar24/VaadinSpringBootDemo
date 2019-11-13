package org.abondar.experimental.vsb.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.abondar.experimental.vsb.model.Person;

import java.util.UUID;


public class AutomaticBind extends VerticalLayout {

    private TextField name = new TextField();
    private Label uuid = new Label();


    public AutomaticBind() {

        Binder<Person> binder = new Binder<>(Person.class);

        var autoLabel = new Label("Automatic binding");
        var person = new Person("Arsen", UUID.randomUUID());

        binder.bindInstanceFields(this);

        add(autoLabel);
        add(name);
        add(uuid);
    }
}
