package com.restblogv2.restblog.controller;


import com.restblogv2.restblog.payload.article.ArticleMetaRequest;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.ArticleMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/articles")
public class ArticleMetaController {

    private final ArticleMetaService articleMetaService;

    @Autowired
    public ArticleMetaController(ArticleMetaService articleMetaService) {
        this.articleMetaService = articleMetaService;
    }

    @GetMapping("{id}/meta")
    public ResponseEntity<?> getMetas(@PathVariable("id") Long articleId){

        return articleMetaService.getAllArticleMetas(articleId);
    }

    @PostMapping("{id}/meta")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> postMeta(@PathVariable("id") Long articleId, @RequestBody ArticleMetaRequest articleMetaRequest, @CurrentUser UserPrincipal currentUser){
        return articleMetaService.postArticleMeta(articleId, articleMetaRequest, currentUser);
    }

    @DeleteMapping("{articleId}/meta/{metaId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteMeta(@PathVariable("articleId") Long articleId, @PathVariable("metaId") Integer metaId, @CurrentUser UserPrincipal currentUser){
        return articleMetaService.deleteArticleMeta(articleId, metaId, currentUser);
    }

}
