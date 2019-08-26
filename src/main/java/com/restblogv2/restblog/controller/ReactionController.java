package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.payload.reaction.ReactionRequest;
import com.restblogv2.restblog.payload.reaction.UpdateReactionRequest;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reactions")
@PreAuthorize("hasRole('USER')")
public class ReactionController {

    private final ReactionService reactionService;

    @Autowired
    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping("")
    public ResponseEntity<?> postReaction(
            @CurrentUser UserPrincipal currentUser,
            @RequestBody ReactionRequest reactionRequest
            ){
        return reactionService.createReaction(currentUser.getId(), reactionRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReacctio(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateReactionRequest updateReactionRequest
            ){
        return reactionService.updateReaction(currentUser, id, updateReactionRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReaction(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable("id") Long id
        ){
        return reactionService.deleteReaction(currentUser, id);
    }


}
