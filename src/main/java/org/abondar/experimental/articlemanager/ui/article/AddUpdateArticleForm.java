package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
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
    private TextField titleField;
    private ComboBox<Author> authorBox;

    public AddUpdateArticleForm(ArticleService articleService, AuthorDataProvider dataProvider) {

        titleField = new TextField();
        titleField.setRequired(true);
        titleField.setRequiredIndicatorVisible(true);
        addFormItem(titleField, "Article Title");


        authorBox = new ComboBox<Author>("Select Author");
        authorBox.setRequired(true);
        authorBox.setRequiredIndicatorVisible(true);
        authorBox.setDataProvider(dataProvider, AuthorFilter::new);
        authorBox.setItemLabelGenerator(Author::getFullName);

        var findLayout = createFindLayout(articleService);
        add(findLayout);

        var coAuthorsBox = new MultiSelectComboBox<Author>("Co-Authors");
        coAuthorsBox.setDataProvider(dataProvider, AuthorFilter::new);
        coAuthorsBox.setItemLabelGenerator(author -> author.getName() + " " + author.getLastName());

        var selectedAuthors = new TextArea("Co-Authors");
        selectedAuthors.setReadOnly(true);

        coAuthorsBox.addValueChangeListener(e -> {
            String selectedCoAuthorName = e.getValue()
                    .stream()
                    .map(Author::getFullName)
                    .collect(Collectors.joining(", "));

            selectedAuthors.setValue(selectedCoAuthorName);
        });

        var coAuthorLayout = new HorizontalLayout(coAuthorsBox, selectedAuthors);
        addFormItem(coAuthorLayout, "Co-Authors");

        var upload = createAndHandleUpload();
        addFormItem(upload, "Upload article");

        var saveButton = new Button("Save", event -> {
            if (isFormValid()) {
                var coAuthorIds = coAuthorsBox.getValue()
                        .stream()
                        .map(Author::getId)
                        .toList();

                //TODO: add edit extensions
                try {
                    articleService.saveAndUploadArticle(titleField.getValue(), authorBox.getValue().getId(),
                            uploadedFile, coAuthorIds);

                    titleField.clear();
                    authorBox.clear();
                    coAuthorsBox.clear();
                    selectedAuthors.clear();
                    uploadedFile = null;
                    upload.clearFileList();

                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    Notification.show("Error while uploading file: " + ex.getMessage(), 3000,
                            Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }


        });
        add(saveButton);
    }

    private HorizontalLayout createFindLayout(ArticleService articleService) {
        var showArticlesByAuthor = new Button("Show articles", event -> {
            var author = authorBox.getValue();

            if (author == null) {
                Notification.show("Please select an author first.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            var articles = articleService.getArticlesByAuthor(author.getId());

            new AuthorArticlesDialog(articles,author).open();
            authorBox.clear();
        });

        var findLayout = new HorizontalLayout(authorBox,showArticlesByAuthor);
        findLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        return findLayout;
    }

    private boolean isFormValid() {
        if (titleField.isEmpty()) {
            Notification.show("Title cannot be empty", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (authorBox.isEmpty()) {
            Notification.show("Please select an author", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (uploadedFile == null) {
            Notification.show("Please upload a file", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        return true;
    }

    private Upload createAndHandleUpload() {
        var buffer = new FileBuffer();
        var upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            var inputStream = buffer.getInputStream();

            uploadedFile = new ArticleFile(inputStream, event.getContentLength());
        });
        return upload;
    }

}
