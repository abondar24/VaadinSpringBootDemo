package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.abondar.experimental.articlemanager.ui.MainLayout;

import java.util.Set;
import java.util.function.BiConsumer;

//TODO: move save and cancel to form
//TODO: move grid to another view
//TODO: add formvalidation after moving save and cancel buttons
@Route(value = "authors", layout = MainLayout.class)
@PageTitle("Article manager/Authors")
@Slf4j
public class AuthorView extends HorizontalLayout {

    private final AuthorService authorService;
    private Author currentAuthor;
    private final AuthorFilter authorFilter;
    private final ConfigurableFilterDataProvider<Author, Void, AuthorFilter> dataProvider;

    public AuthorView(AuthorService authorService, AuthorAddUpdateForm authorAddUpdateForm,
                      AuthorDataProvider authorDataProvider) {
        this.authorService = authorService;

        this.authorFilter = new AuthorFilter();

        this.currentAuthor = new Author();

        this.dataProvider = authorDataProvider.withConfigurableFilter();

        var formLayout = new VerticalLayout();

        var authorGrid = createGrid();

        var saveButton = new Button("Save", click -> {
            try {
                authorAddUpdateForm.getBinder().writeBean(currentAuthor);
                Author savedAuthor;

                if (currentAuthor.getId() == null) {
                    savedAuthor = authorService.save(currentAuthor.getName(), currentAuthor.getLastName(),
                            currentAuthor.getEmail());
                    log.info("Saved author {}", savedAuthor.getId());
                } else {
                    savedAuthor = authorService.updateAuthor(currentAuthor);
                    log.info("Updated author {}", savedAuthor.getId());
                }

                authorGrid.getDataProvider().refreshAll();
                clearForm(authorAddUpdateForm);

            } catch (ValidationException e) {
                log.error(e.getMessage());
                Notification.show("Please correct the errors", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        var cancelButton = new Button("Cancel", click -> clearForm(authorAddUpdateForm));

        var searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            authorFilter.setSearchTerm(e.getValue());
            dataProvider.setFilter(authorFilter);
        });

        authorGrid.addComponentColumn(at -> {
            var deleteBtn = new Button("", click -> {
                authorService.deleteAuthor(at.getId());
                Notification.show("Author deleted", 3000, Notification.Position.TOP_CENTER);
                log.info("Deleted author {}", at.getId());
                authorGrid.getDataProvider().refreshAll();  // Refresh grid after deletion
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            deleteBtn.setIcon(new Icon(VaadinIcon.TRASH));
            deleteBtn.setTooltipText("Delete author");

            var connectionsBtn = new Button("", click -> {
                var connections = authorService.findConnectionsById(at.getId());
                var connectionsDialog = new AuthorConnectionsDialog(connections);
                connectionsDialog.open();
            });
            connectionsBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            connectionsBtn.setIcon(new Icon(VaadinIcon.CONNECT));
            connectionsBtn.setTooltipText("View connected authors");

            var updateAuthorBtn = new Button("", click -> {
                currentAuthor = authorService.getAuthorById(at.getId());
                authorAddUpdateForm.getBinder().readBean(currentAuthor);
            });
            updateAuthorBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY,
                    ButtonVariant.LUMO_CONTRAST);
            updateAuthorBtn.setIcon(new Icon(VaadinIcon.EDIT));
            updateAuthorBtn.setTooltipText("Update author");


            var actionsLayout = new HorizontalLayout();
            actionsLayout.add(connectionsBtn, updateAuthorBtn, deleteBtn);

            return actionsLayout;
        }).setHeader("Manage").setAutoWidth(true);

        var connectLayout = getConnectLayout(authorService, authorGrid);

        var gridLayout = new VerticalLayout();
        gridLayout.add(searchField, authorGrid, connectLayout);

        var submitLayout = new HorizontalLayout();
        submitLayout.add(saveButton, cancelButton);

        formLayout.add(authorAddUpdateForm, submitLayout);
        add(formLayout, gridLayout);
    }

    private HorizontalLayout getConnectLayout(AuthorService authorService, Grid<Author> authorGrid) {
        var connectAuthors = new Button("Connect Authors", click ->
                handleConnectionAction(authorGrid.getSelectedItems(), authorService::connectAuthors,
                        AuthorConnectAction.CONNECTED, NotificationVariant.LUMO_SUCCESS));

        var disconnectAuthors = new Button("Disconnect Authors", click ->
                handleConnectionAction(authorGrid.getSelectedItems(), authorService::disconnectAuthors,
                        AuthorConnectAction.DISCONNECTED, NotificationVariant.LUMO_WARNING));

        var connectLayout = new HorizontalLayout();
        connectLayout.add(connectAuthors, disconnectAuthors);
        return connectLayout;
    }

    private void clearForm(AuthorAddUpdateForm authorAddUpdateForm) {
        currentAuthor = new Author();
        authorAddUpdateForm.getBinder().readBean(currentAuthor);
    }

    private Grid<Author> createGrid() {
        var authorGrid = new Grid<>(Author.class);
        authorGrid.setColumns("name", "lastName", "email");

        authorGrid.getColumnByKey("name").setResizable(true);
        authorGrid.getColumnByKey("lastName").setResizable(true);
        authorGrid.getColumnByKey("email").setResizable(true);

        authorGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        authorGrid.setWidthFull();
        authorGrid.setHeight("400px");


        authorGrid.setDataProvider(dataProvider);
        return authorGrid;
    }

    private void handleConnectionAction(Set<Author> selected, BiConsumer<String, String> action,
                                        AuthorConnectAction actionName, NotificationVariant notificationVariant) {
        if (selected.size() != 2) {
            Notification.show("Only two authors can be" + actionName + " at once", 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        var iterator = selected.iterator();
        var author1Id = iterator.next().getId();
        var author2Id = iterator.next().getId();

        var connectionExists = authorService.connectionExists(author1Id, author2Id);

        if ((connectionExists && actionName == AuthorConnectAction.CONNECTED) ||
                (!connectionExists && actionName == AuthorConnectAction.DISCONNECTED)) {
            Notification.show("Authors are already " + actionName.name().toLowerCase(), 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            return;
        }


        action.accept(author1Id, author2Id);

        log.info("Connected author {} and author {}", author1Id, author2Id);
        Notification.show("Authors " + actionName, 3000,
                Notification.Position.TOP_CENTER).addThemeVariants(notificationVariant);

    }

}
