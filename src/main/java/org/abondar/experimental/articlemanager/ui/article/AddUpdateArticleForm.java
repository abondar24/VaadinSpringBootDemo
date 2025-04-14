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
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.ArticleService;
import org.abondar.experimental.articlemanager.ui.author.AuthorDataProvider;
import org.abondar.experimental.articlemanager.ui.author.AuthorFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
@Slf4j
public class AddUpdateArticleForm extends FormLayout {

    private final TextField titleField;
    private final ComboBox<Author> authorBox;
    private final MultiSelectComboBox<Author> coAuthorsBox;
    private final TextArea selectedAuthors;
    private final Upload upload;
    @Getter
    private final Binder<Article> binder;
    private ArticleFile uploadedFile;
    private final ArticleService articleService;

    @Setter
    private ArticleGrid articleGrid;

    public AddUpdateArticleForm(ArticleService articleService, AuthorDataProvider dataProvider) {
        this.articleService = articleService;
        binder = new Binder<>(Article.class);

        titleField = new TextField();
        titleField.setRequired(true);
        titleField.setRequiredIndicatorVisible(true);
        binder.forField(titleField)
                .asRequired("Title is required")
                .bind(Article::getTitle, Article::setTitle);

        addFormItem(titleField, "Article Title");

        authorBox = new ComboBox<>("Select Author");
        authorBox.setRequired(true);
        authorBox.setRequiredIndicatorVisible(true);
        authorBox.setDataProvider(dataProvider, AuthorFilter::new);
        authorBox.setItemLabelGenerator(Author::getFullName);
        binder.forField(authorBox)
                .asRequired("Author is required")
                .bind(Article::getAuthor, Article::setAuthor);
        addFormItem(authorBox, "Author");

        coAuthorsBox = new MultiSelectComboBox<>("Co-Authors");
        coAuthorsBox.setDataProvider(dataProvider, AuthorFilter::new);
        coAuthorsBox.setItemLabelGenerator(Author::getFullName);


        selectedAuthors = new TextArea("Selected Co-Authors"); // Clarify label
        selectedAuthors.setReadOnly(true);

        coAuthorsBox.addValueChangeListener(e -> {
            String selectedCoAuthorNames = e.getValue()
                    .stream()
                    .map(Author::getFullName)
                    .collect(Collectors.joining(", "));
            selectedAuthors.setValue(selectedCoAuthorNames);
        });
        binder.forField(coAuthorsBox)
                .bind(article -> new HashSet<>(article.getCoAuthors()),
                        (article, coAuthors) -> article.setCoAuthors(new ArrayList<>(coAuthors)));

        var coAuthorLayout = new HorizontalLayout(coAuthorsBox, selectedAuthors);
        coAuthorLayout.setWidthFull();
        coAuthorsBox.setWidth("50%");
        selectedAuthors.setWidth("50%");
        addFormItem(coAuthorLayout, "Co-Authors");

        upload = createAndHandleUpload();
        addFormItem(upload, "Upload Article File");

        Button saveButton = new Button("Save", event -> saveArticle());
        Button clearButton = new Button("Clear", event -> clearForm());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, clearButton);
        add(buttons);

        binder.setBean(new Article());
    }

    public void editArticle(Article article) {
        log.info("Editing article: {}", article.getId());
        this.binder.setBean(article);

        coAuthorsBox.setValue(new HashSet<>(article.getCoAuthors()));

        String selectedCoAuthorNames = article.getCoAuthors()
                .stream()
                .map(Author::getFullName)
                .collect(Collectors.joining(", "));
        selectedAuthors.setValue(selectedCoAuthorNames);

        uploadedFile = null;
        upload.clearFileList();
    }


    private void saveArticle() {
        try {
            Article articleToSave = binder.getBean();

            if (isFormValid(articleToSave)) {
                binder.writeBean(articleToSave);

                var coAuthorIds = coAuthorsBox.getValue()
                        .stream()
                        .map(Author::getId)
                        .toList();

                if (articleToSave.getId() != null) {
                    log.info("Updating article ID: {}", articleToSave.getId());
                    articleService.updateArticle(articleToSave, uploadedFile, coAuthorIds);
                    Notification.show("Article updated successfully", 3000, Notification.Position.TOP_CENTER);
                } else {
                    log.info("Saving new article with title: {}", articleToSave.getTitle());
                    articleService.saveAndUploadArticle(articleToSave.getTitle(), articleToSave.getAuthor().getId(),
                            uploadedFile, coAuthorIds);
                    Notification.show("Article saved successfully", 3000, Notification.Position.TOP_CENTER);
                }

                clearForm();
                if (articleGrid != null) {
                    articleGrid.refresh();
                } else {
                    log.warn("ArticleGrid reference is null, cannot refresh grid.");
                }
            }
        } catch (ValidationException vex) {
            log.error("Validation failed during writeBean: {}", vex.getMessage());
            Notification.show("Validation Error: Please check the form values.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            log.error("Error saving/updating article: {}", ex.getMessage(), ex); // Log stack trace
            Notification.show("Error processing article: " + ex.getMessage(), 3000,
                    Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

     private boolean isFormValid(Article article) {
        if (!binder.validate().isOk()){
            Notification.show("Please fix validation errors indicated on the form.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

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

        if (uploadedFile == null && (article == null || article.getId() == null)) {
            Notification.show("Please upload a file for a new article", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }


        return true;
    }

    private Upload createAndHandleUpload() {
        var buffer = new FileBuffer();
        var upload = new Upload(buffer);
        upload.setMaxFiles(1); // Allow only one file

        upload.addSucceededListener(event -> {
            log.info("File upload succeeded: {}", event.getFileName());
            var inputStream = buffer.getInputStream();
            var mimeType = event.getMIMEType();
            var originalFilename = event.getFileName();
            uploadedFile = new ArticleFile(inputStream, event.getContentLength(), mimeType, originalFilename);
        });

        return upload;
    }

    public void clearForm() {
        titleField.clear();
        authorBox.clear();
        coAuthorsBox.clear();
        coAuthorsBox.setValue(Set.of());
        selectedAuthors.clear();
        uploadedFile = null;
        upload.clearFileList();

        binder.setBean(new Article());
    }
}