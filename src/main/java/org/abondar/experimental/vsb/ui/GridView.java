package org.abondar.experimental.vsb.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.router.Route;
import org.abondar.experimental.vsb.model.Person;

import java.util.List;
import java.util.UUID;

@Route("grid")
public class GridView extends VerticalLayout {

    public GridView() {

        var people = List.of(new Person("Arsen", UUID.randomUUID()),
                new Person("Vagan", UUID.randomUUID()),
                new Person("Gagik", UUID.randomUUID()));

        Grid<Person> grid = new Grid<>();
        grid.setItems(people);
        grid.addColumn(Person::getName).setHeader("Name");
        grid.addColumn(Person::getUuid).setHeader("Id");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);


        /*
        //for reference
        grid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    //fetch data.

                    return people.stream();
                },
                query -> {
                    //count
                    return 100;
                }
        ));

         */

        MultiSelect<Grid<Person>, Person> multiSelect = grid.asMultiSelect();
        multiSelect.addSelectionListener(e->e.getValue().forEach(p->System.out.println(p.getUuid())));

        grid.addItemClickListener(e->System.out.println(e.getItem().getName()));

        Grid.Column<Person> custom = grid.addColumn(Person::isActive)
                .setHeader("Active")
                .setFrozen(true)
                .setWidth("20px");

        grid.addColumn(new ComponentRenderer<>(person -> {
            var textField = new TextField();
            textField.addValueChangeListener(
                    event -> person.setName(event.getValue()));

            return textField;
        })).setHeader("Text");

        grid.addThemeNames("no-border");

        add(grid);
    }
}
