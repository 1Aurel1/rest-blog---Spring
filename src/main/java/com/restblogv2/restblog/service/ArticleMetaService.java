package com.restblogv2.restblog.service;

import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.article.ArticleMeta;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.payload.article.ArticleMetaRequest;
import com.restblogv2.restblog.repository.ArticleMetaRepositiory;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class ArticleMetaService {

    private final ArticleMetaRepositiory articleMetaRepositiory;
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleMetaService(ArticleMetaRepositiory articleMetaRepositiory, ArticleRepository articleRepository) {
        this.articleMetaRepositiory = articleMetaRepositiory;
        this.articleRepository = articleRepository;
    }

    public ResponseEntity<?> getAllArticleMetas(Long articleId) {
        return new ResponseEntity<>(articleMetaRepositiory.findAllByArticleId(articleId), HttpStatus.OK);
    }

    public ResponseEntity<?> postArticleMeta(Long articleId, ArticleMetaRequest articleMetaRequest, UserPrincipal currentUser) {

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        if (article.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            ArticleMeta articleMeta = new ArticleMeta();
            articleMeta.setMetaKey(articleMetaRequest.getMetaKey());
            articleMeta.setMetaValue(articleMetaRequest.getMetaValue());
            articleMeta.setArticle(article);

            return new ResponseEntity<>(articleMetaRepositiory.save(articleMeta), HttpStatus.CREATED);

        }

        return new ResponseEntity<>(new ApiResponse(false, "You are unauthorised"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteArticleMeta(Long articleId, int id, UserPrincipal currentUser) {
        ArticleMeta articleMeta = articleMetaRepositiory.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article Meta", "id", id));
        if (articleId == articleMeta.getArticle().getId()){
            return new ResponseEntity<>(new ApiResponse(false, "This meta does not bellong to this article"), HttpStatus.BAD_REQUEST);
        }
        if ( articleMeta.getArticle().getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {

            articleMetaRepositiory.delete(articleMeta);

            return new ResponseEntity<>(new ApiResponse(true, "ArticleMeta deleted successfully!"), HttpStatus.OK);

        }
        return new ResponseEntity<>(new ApiResponse(false, "You are unauthorised"), HttpStatus.UNAUTHORIZED);
    }



}
