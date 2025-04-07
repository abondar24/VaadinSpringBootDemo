package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.abondar.experimental.articlemanager.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.function.BiConsumer;


@Route(value = "authors", layout = MainLayout.class)
@PageTitle("Article manager | Authors")
@Slf4j
public class AuthorView extends HorizontalLayout {

    private final AuthorGrid authorGrid;
    private final AuthorAddUpdateForm authorAddUpdateForm;
    private final AuthorService authorService;

    private TextField searchField;

    @Autowired
    public AuthorView(AuthorGrid authorGrid, AuthorAddUpdateForm authorAddUpdateForm, AuthorService authorService) {
        this.authorGrid = authorGrid;
        this.authorAddUpdateForm = authorAddUpdateForm;
        this.authorService = authorService;

        setSizeFull();
        configureLayout();
    }

    @PostConstruct
    private void init() {
        authorAddUpdateForm.setAuthorGrid(authorGrid);
        setupSearchListener();
    }

    private void configureLayout() {
        this.searchField = createSearchField();
        var connectLayout = createConnectLayout();

        var gridLayout = new VerticalLayout();
        gridLayout.add(searchField, authorGrid, connectLayout);
        gridLayout.setSizeFull();
        gridLayout.setPadding(false);
        gridLayout.setSpacing(true);
        gridLayout.setFlexGrow(1, authorGrid);


        authorAddUpdateForm.setWidth("30%");
        authorAddUpdateForm.setHeightFull();

        gridLayout.setWidth("70%");
        gridLayout.setHeightFull();

        add(authorAddUpdateForm, gridLayout);
    }

    private TextField createSearchField() {
        var search = new TextField();
        search.setWidth("50%");
        search.setPlaceholder("Search by Name or Email");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        return search;
    }

    private void setupSearchListener() {
        searchField.addValueChangeListener(e -> {
            authorGrid.getAuthorFilter().setSearchTerm(e.getValue());
            authorGrid.getDataProvider().setFilter(authorGrid.getAuthorFilter());
            log.debug("Filtering authors view with term: {}", e.getValue());
        });
    }


    private HorizontalLayout createConnectLayout() {
        var connectAuthors = new Button("Connect Selected", click ->
                handleConnectionAction(authorGrid.getSelectedItems(), authorService::connectAuthors,
                        AuthorConnectAction.CONNECTED, NotificationVariant.LUMO_SUCCESS));

        var disconnectAuthors = new Button("Disconnect Selected", click ->
                handleConnectionAction(authorGrid.getSelectedItems(), authorService::disconnectAuthors,
                        AuthorConnectAction.DISCONNECTED, NotificationVariant.LUMO_WARNING));

        var layout = new HorizontalLayout();
        layout.add(connectAuthors, disconnectAuthors);
        return layout;
    }

    private void handleConnectionAction(Set<Author> selected, BiConsumer<String, String> action,
                                        AuthorConnectAction actionName, NotificationVariant notificationVariant) {
        if (selected.size() != 2) {
            Notification.show("Select exactly two authors to " + actionName.toString().toLowerCase(), 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        var iterator = selected.iterator();
        var author1Id = iterator.next().getId();
        var author2Id = iterator.next().getId();

        try {
            var connectionExists = authorService.connectionExists(author1Id, author2Id);

            if (connectionExists && actionName == AuthorConnectAction.CONNECTED) {
                Notification.show("Authors are already connected", 3000,
                        Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                return;
            }
            if (!connectionExists && actionName == AuthorConnectAction.DISCONNECTED) {
                Notification.show("Authors are already disconnected", 3000,
                        Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                return;
            }

            action.accept(author1Id, author2Id);

            log.info("Authors {} and {} {}", author1Id, author2Id, actionName.name().toLowerCase());
            Notification.show("Authors " + actionName.name().toLowerCase(), 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(notificationVariant);

        } catch (Exception e) {
            log.error("Error performing connection action ({}) between {} and {}", actionName, author1Id, author2Id, e);
            Notification.show("Error " + actionName.name().toLowerCase() + " authors: " + e.getMessage(), 5000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

}