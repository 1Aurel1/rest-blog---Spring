package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.dto.ArticlePositionDto;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.AdminService;
import com.restblogv2.restblog.util.FileStorage;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final FileStorage fileStorageService;

    @Autowired
    public AdminController(AdminService adminService, FileStorage fileStorageService) {
        this.adminService = adminService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/articles/unathorised")
    public ResponseEntity<?> getAllUnAuthorisedArticles(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser){
        return adminService.getUnAuthorisedArticles(page, size, currentUser);
    }

    @PutMapping("/articles/{id}/authorise")
    public ResponseEntity<?> changeAuthorisationForArticle(
            @PathVariable("id") Long article_id,
            @RequestParam(name = "authorised", required = true) boolean authorised,
            @CurrentUser UserPrincipal currentUser
            ){
        return adminService.authoriseArticles(article_id, authorised, currentUser);
    }

    @PutMapping("/articles/updatePositions")
    public ResponseEntity<?> updateArticlesPositions(
            @Valid @RequestBody List<ArticlePositionDto> articlesPositionsDto,
            @CurrentUser UserPrincipal currentUser
            ){

        return adminService.updateArticlesPositions(articlesPositionsDto, currentUser);
    }

    @PutMapping("/comments/{id}/changeStatus")
    public ResponseEntity<?> changeCommentStatus(
            @PathVariable("id") Long id,
            @RequestParam("authorised") boolean authorised
        ){
        return adminService.enableComment(id, authorised);
    }


}
