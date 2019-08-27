package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.payload.dto.ArticleDto;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.payload.PagedResponse;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.ArticleService;
import com.restblogv2.restblog.util.AppConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api("Cateogies")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @ApiOperation("View all available articles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
        }
    )
    @GetMapping
    public PagedResponse<Article> getAllArticles(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return articleService.getAllArticlesShowalbe(page, size);
    }

    @ApiOperation("View article")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved article"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable(name = "id") Long id){
        return articleService.getArticle(id);
    }

    @ApiOperation("Create a new article")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created"),
            @ApiResponse(code = 403, message = "Authorisation required"),
    }
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addArticle(@Valid @RequestBody Article article, @CurrentUser UserPrincipal currentUser){
        return articleService.addArticle(article, currentUser);
    }

    @ApiOperation("Update article")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Authorisation required"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateArticle(@PathVariable(name = "id") Long id, @Valid @RequestBody ArticleDto newArticle, @CurrentUser UserPrincipal currentUser) throws Exception {
        return articleService.updateArticle(id, newArticle, currentUser);
    }

    @ApiOperation("Delete article")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted"),
            @ApiResponse(code = 401, message = "You are not authorized to delete the resource"),
            @ApiResponse(code = 403, message = "Authorisation required"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteArticle(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return articleService.deleteArticle(id, currentUser);
    }
}
