package org.abondar.experimental.articlemanager.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.abondar.experimental.articlemanager.ui.article.ArticleView;
import org.abondar.experimental.articlemanager.ui.author.AuthorView;

@Route("")
@PageTitle("Article manager")
@Layout
public class MainLayout extends AppLayout implements RouterLayout {


    public MainLayout() {
        H1 title = new H1("Article Manager");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        var toggle = new DrawerToggle();
        addToNavbar(toggle, title);

        var nav = new SideNav();
        SideNavItem authorItem = new SideNavItem("Authors", AuthorView.class, VaadinIcon.ACADEMY_CAP.create());
        SideNavItem articleItem = new SideNavItem("Articles", ArticleView.class, VaadinIcon.BOOK.create());
        nav.addItem(authorItem, articleItem);

        var scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        addToDrawer(scroller);

        var themeToggleButton = new Button("🌙 Dark Mode", event -> {
            getUI().ifPresent(ui -> {
                boolean isDarkMode = ui.getElement().getThemeList().contains("dark");
                if (isDarkMode) {
                    ui.getElement().getThemeList().remove("dark");
                    event.getSource().setText("🌙 Dark Mode");
                } else {
                    ui.getElement().getThemeList().add("dark");
                    event.getSource().setText("☀️ Light Mode");
                }
            });
        });
        themeToggleButton.getStyle()
                .set("background", "transparent")
                .set("border", "none")
                .set("cursor", "pointer");

        addToNavbar(themeToggleButton);
    }


}

