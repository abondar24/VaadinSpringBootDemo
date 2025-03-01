package org.abondar.experimental.articlemanager.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.exception.ArticleNotFoundException;
import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final AuthorRepository authorRepository;

    private final FileUploadService fileUploadService;

    public Article saveAndUploadArticle(String title, String authorId, MultipartFile file, List<String> coAuthorsIds) throws Exception {
        var id = UUID.randomUUID().toString();
        var articleKey = authorId + "/" + id;

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));

        var article = new Article(id, title, articleKey, author, getCoAuthors(coAuthorsIds));

        fileUploadService.uploadFile(articleKey, file);
        return articleRepository.save(article);
    }


    public List<Article> getArticlesByAuthor(String authorId) {
        return articleRepository.findArticlesByAuthor(authorId);
    }

    public Article updateArticle(Article article, MultipartFile file, List<String> coAuthorsIds) throws Exception {
         getArticle(article.getId());

        if (file != null && !file.isEmpty()) {
            fileUploadService.uploadFile(article.getArticleKey(), file);
        }

        var coAuthors = getCoAuthors(coAuthorsIds);
        if (!coAuthors.isEmpty()) {
            article.getCoAuthors().addAll(coAuthors);
        }

        return articleRepository.save(article);
    }

    public void deleteArticle(String articleId) throws Exception {
        var article = getArticle(articleId);

        fileUploadService.deleteFile(article.getArticleKey());
        articleRepository.delete(article);
    }

    private Article getArticle(String articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));
    }


    private List<Author> getCoAuthors(List<String> coAuthorsIds) {
        List<Author> coAuthors = new ArrayList<>();
        if (coAuthorsIds != null && !coAuthorsIds.isEmpty()) {
            coAuthors = authorRepository.findByIds(coAuthorsIds);
        }
        return coAuthors;
    }

}
