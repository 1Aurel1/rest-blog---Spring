package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.payload.dto.CommentDto;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@PreAuthorize("hasRole('USER')")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("")
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentDto commentDto,
            @CurrentUser UserPrincipal currentUser
            ){
        commentDto.setUserId(currentUser.getId());
        return commentService.createComment(commentDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable("id") Long id,
            @Valid @RequestBody CommentDto commentDto,
            @CurrentUser UserPrincipal currentUser
            ){
        return commentService.updateComment(id, commentDto, currentUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id, @CurrentUser UserPrincipal currentUser){
        return commentService.deleteComment(id, currentUser);
    }

}
