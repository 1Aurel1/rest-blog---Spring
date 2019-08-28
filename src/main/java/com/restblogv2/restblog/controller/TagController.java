package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.model.tag.Tag;
import com.restblogv2.restblog.payload.PagedResponse;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.TagService;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    public PagedResponse<Tag> getAllTags(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return tagService.getAllTags(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable(name = "id") Long id){
        return tagService.getTag(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPost(@Valid @RequestBody Tag tag, @CurrentUser UserPrincipal currentUser){
        return tagService.addTag(tag, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteTag(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return tagService.deleteTag(id, currentUser);
    }

    @GetMapping("/{tag}/articles")
    public ResponseEntity<?> getAllArticlesWithTag(
            @PathVariable("tag") String tagName
        ){
        return tagService.getArticlesWithTag(tagName);
    }

    @GetMapping("/{tag}/exists")
    public ResponseEntity<?> existsTag(@PathVariable("tag") String tag){
        return tagService.exists(tag);
    }

}
