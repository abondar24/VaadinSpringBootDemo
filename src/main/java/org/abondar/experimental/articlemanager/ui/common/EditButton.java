package org.abondar.experimental.articlemanager.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditButton  extends Button {

    public EditButton(ComponentEventListener<ClickEvent<Button>> clickEvent,String tooltip) {
        addClickListener(clickEvent);

        addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY,
                ButtonVariant.LUMO_CONTRAST);
        setIcon(new Icon(VaadinIcon.EDIT));
        setTooltipText(tooltip);
    }
}
