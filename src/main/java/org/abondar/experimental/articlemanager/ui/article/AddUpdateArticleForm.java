package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.ArticleService;
import org.abondar.experimental.articlemanager.ui.author.AuthorDataProvider;
import org.abondar.experimental.articlemanager.ui.author.AuthorFilter;

import java.util.stream.Collectors;

@SpringComponent
@UIScope
@Route(value = "article/add")
@PageTitle("Manage article")
@Slf4j
public class AddUpdateArticleForm extends FormLayout {

    private ArticleFile uploadedFile;

    public AddUpdateArticleForm(ArticleService articleService, AuthorDataProvider dataProvider) {

        var titleField = new TextField();
        addFormItem(titleField, "Article Title");


        var authorBox = new ComboBox<Author>("Select Author");
        authorBox.setDataProvider(dataProvider, AuthorFilter::new);
        authorBox.setItemLabelGenerator(a -> a.getName() + " " + a.getLastName());
        addFormItem(authorBox, "Author");

        var coAuthorsBox = new MultiSelectComboBox<Author>("Co-Authors");
        coAuthorsBox.setDataProvider(dataProvider, AuthorFilter::new);
        coAuthorsBox.setItemLabelGenerator(author -> author.getName() + " " + author.getLastName());

        var selectedAuthors = new TextArea("Co-Authors");
        selectedAuthors.setReadOnly(true);

        coAuthorsBox.addValueChangeListener(e -> {
            String selectedCoAuthorName = e.getValue().stream()
                    .map(a -> a.getName() + " " + a.getLastName()).collect(Collectors.joining(", "));

            selectedAuthors.setValue(selectedCoAuthorName);
        });

        var coAuthorLayout = new HorizontalLayout(coAuthorsBox, selectedAuthors);
        addFormItem(coAuthorLayout, "Co-Authors");


        var buffer = new MultiFileMemoryBuffer();
        var upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            var inputStream = buffer.getInputStream(fileName);

            uploadedFile = new ArticleFile(inputStream, event.getContentLength());
        });
        addFormItem(upload, "Upload article");

        var saveButton = new Button("Save", event -> {
            var coAuthorIds = coAuthorsBox.getValue()
                    .stream()
                    .map(Author::getId)
                    .toList();

            //TODO: create bucket in localstack
            //TODO: clear form on save
            //TODO: add edit extensions
            try {
                articleService.saveAndUploadArticle(titleField.getTitle(), authorBox.getValue().getId(),
                        uploadedFile, coAuthorIds);
            } catch (Exception ex) {
                log.error(ex.getMessage());
                Notification.show("Error while uploading file: " + ex.getMessage(), 3000,
                        Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        add(saveButton);
    }


}
