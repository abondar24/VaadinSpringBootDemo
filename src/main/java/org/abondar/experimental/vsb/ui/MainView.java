package org.abondar.experimental.vsb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@Route("")
@Theme(value = Material.class, variant = Material.LIGHT)
public class MainView extends AppLayout {

    public MainView() {

        var button = new Button("Super button", event->Notification.show("Hii"));
        var textField = new TextField();
        var greeting = new Span("Welcome back");

        textField.addValueChangeListener(event->greeting.setText(event.getValue()));
        textField.setLabel("Enter a greeting");
        textField.setPlaceholder("greeting");

        var emailField = new EmailField("Email");
        emailField.addValueChangeListener(event -> greeting.setText(event.getValue()));

        var priceField = new NumberField("Price");
        priceField.setPrefixComponent(new Span("$"));
        priceField.setSuffixComponent(new Span("%"));
        priceField.setMin(0);
        priceField.setMax(10);
        priceField.setHasControls(true);

        var progressBar = new ProgressBar(0.1,1.0,0.777);

        var gridLink = new RouterLink("Grid view",GridView.class);

        var bindBtn = new Button("Bind view",event->{
            UI.getCurrent().navigate(BindView.class);
        });

        addToDrawer(button);

        var horizontalLayout = new HorizontalLayout();

        addToDrawer(horizontalLayout);
        horizontalLayout.add(textField);
        horizontalLayout.addAndExpand(emailField);
        horizontalLayout.add(greeting);
        addToDrawer(priceField);
        addToDrawer(progressBar);
        addToDrawer(gridLink);
        addToDrawer(bindBtn);

        addToNavbar(new Span("Vaadin 14 Demo"));


    }
}
