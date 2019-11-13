package org.abondar.experimental.vsb.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.abondar.experimental.vsb.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.UUID;

@Route("bind")
public class BindView extends VerticalLayout {

    private Logger logger = LoggerFactory.getLogger(BindView.class);

    public BindView(){

        Binder<Person> binder = new Binder<>(Person.class);

        var nameField = new TextField();
        binder.forField(nameField)
                .withValidator(name-> name.length() >=2,"Name must be long enough")
                .asRequired("Name is important")
                .bind(Person::getName,Person::setName);

        var person = new Person("Arsen", UUID.randomUUID());

        //automatic: shows updates immediately. better to use
        binder.setBean(person);

        //manual
        //binder.readBean(person);

        var save = new Button("Save",e->{
            try {
                binder.writeBean(person);
                //save to db code
                System.out.println(person);
            } catch(ValidationException ex){
                logger.error(ex.getMessage());
            }
        });

        var idLabel = new Label();
        ReadOnlyHasValue<String> idRead = new ReadOnlyHasValue<>(idLabel::setText);
        binder.forField(idRead).bind(p->p.getUuid().toString(),null);




        add(nameField);
        add(idLabel);
        add(save);

        var auto = new AutomaticBind();
        add(auto);

    }
}
