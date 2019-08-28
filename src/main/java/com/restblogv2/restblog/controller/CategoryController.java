package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.payload.dto.CategoryDto;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.CategoryService;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAllArticles(){
        return categoryService.getAllCategories();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> postCategory(@Valid @RequestBody CategoryDto categoryDto){
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<?> getCategoryArticles(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
            ){
        return categoryService.getCategoryArticles(id, page, size);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryDto updatedCategory,
            @CurrentUser UserPrincipal currentUser
            ){
        return categoryService.updateCategory(id, updatedCategory, currentUser);
    }

}
