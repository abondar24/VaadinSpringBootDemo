package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.abondar.experimental.articlemanager.ui.common.DeleteButton;
import org.abondar.experimental.articlemanager.ui.common.EditButton;

@SpringComponent
@UIScope
@Slf4j
public class AuthorGrid extends Grid<Author> {

    private static final String NAME_COLUMN = "name";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL_COLUMN = "email";

    @Getter
    private final AuthorFilter authorFilter;

    @Getter
    private final ConfigurableFilterDataProvider<Author, Void, AuthorFilter> dataProvider;

    public AuthorGrid(AuthorService authorService, AuthorAddUpdateForm authorAddUpdateForm,
                      AuthorDataProvider authorDataProvider) {
        super(Author.class);
        this.authorFilter = new AuthorFilter();
        this.dataProvider = authorDataProvider.withConfigurableFilter();

        setColumns(NAME_COLUMN, LAST_NAME, EMAIL_COLUMN);

        getColumnByKey(NAME_COLUMN).setResizable(true);
        getColumnByKey(LAST_NAME).setResizable(true);
        getColumnByKey(EMAIL_COLUMN).setResizable(true);

        setSelectionMode(Grid.SelectionMode.MULTI);
        setWidthFull();
        setHeight("400px");

        setDataProvider(dataProvider);

        addComponentColumn(at -> {
            var deleteBtn = new DeleteButton(click -> {
                authorService.deleteAuthor(at.getId());
                Notification.show("Author deleted", 3000, Notification.Position.TOP_CENTER);
                log.info("Deleted author {}", at.getId());

                if (authorAddUpdateForm.getBinder().getBean() != null &&
                        at.getId().equals(authorAddUpdateForm.getBinder().getBean().getId())) {
                    authorAddUpdateForm.clearForm();
                }

            });

            var connectionsBtn = new Button("", click -> {
                var connections = authorService.findConnectionsById(at.getId());
                var connectionsDialog = new AuthorConnectionsDialog(connections);
                connectionsDialog.open();
            });
            connectionsBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            connectionsBtn.setIcon(new Icon(VaadinIcon.CONNECT));
            connectionsBtn.setTooltipText("View connected authors");

            var editBtn = new EditButton(click -> {
                var authorToEdit = authorService.getAuthorById(at.getId());
                authorAddUpdateForm.setAuthorToEdit(authorToEdit);
            }, "Edit author");

            var actionsLayout = new HorizontalLayout();
            actionsLayout.add(connectionsBtn, editBtn, deleteBtn);
            actionsLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            actionsLayout.setSpacing(false);
            actionsLayout.setPadding(false);

            return actionsLayout;
        }).setHeader("Manage")
                .setAutoWidth(true);


    }

    public void refresh() {
        getDataProvider().refreshAll();

    }

}
