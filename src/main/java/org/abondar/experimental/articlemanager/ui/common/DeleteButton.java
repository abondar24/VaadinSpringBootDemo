package org.abondar.experimental.articlemanager.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteButton extends Button {

    public DeleteButton(ComponentEventListener<ClickEvent<Button>> clickEvent) {
        addClickListener(clickEvent);

        addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        setIcon(new Icon(VaadinIcon.TRASH));
        setTooltipText("Delete author");
    }
}
