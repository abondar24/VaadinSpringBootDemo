package org.abondar.experimental.articlemanager.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.exception.ArticleNotFoundException;
import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.ArticleProjection;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.model.ArticleFile;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.springframework.stereotype.Service;

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

    public Article saveAndUploadArticle(String title, String authorId, ArticleFile articleFile, List<String> coAuthorsIds) throws Exception {
        var id = UUID.randomUUID().toString();
        var articleKey = authorId + "/" + id;

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));

        var article = new Article(id, title, articleKey, author, getCoAuthors(coAuthorsIds));

        fileUploadService.uploadFile(articleKey, articleFile);
        return articleRepository.save(article);
    }

    public List<Article> getArticlesByAuthor(String authorId) {
        return articleRepository.findArticleByAuthor_Id(authorId);
    }

    //TODO: add to ui
    public Article updateArticle(Article article, ArticleFile articleFile, List<String> coAuthorsIds) throws Exception {
        getArticle(article.getId());

        if (articleFile != null) {
            fileUploadService.uploadFile(article.getArticleKey(), articleFile);
        }

        var coAuthors = getCoAuthors(coAuthorsIds);
        if (!coAuthors.isEmpty()) {
            article.getCoAuthors().addAll(coAuthors);
        }

        return articleRepository.save(article);
    }

    public List<ArticleProjection> getArticles(int offset, int limit){
          return articleRepository.findArticles(offset, limit);
    }

    public void deleteArticle(String articleId) {
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
