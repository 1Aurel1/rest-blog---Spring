package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.payload.PagedResponse;
import com.restblogv2.restblog.payload.auth.InfoRequest;
import com.restblogv2.restblog.payload.user.UserIdentityAvailability;
import com.restblogv2.restblog.payload.user.UserProfile;
import com.restblogv2.restblog.payload.user.UserSummary;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.ArticleService;
import com.restblogv2.restblog.service.UserService;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ArticleService articleService;

    @Autowired
    public UserController(UserService userService, ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        return userService.getCurrentUser(currentUser);
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username){
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email){
        return userService.checkEmailAvailability(email);
    }

    @GetMapping("/{username}/profile")
    public UserProfile getUSerProfile(@PathVariable(value = "username") String username){
        return userService.getUserProfile(username);
    }

    @GetMapping("/{username}/articles")
    public PagedResponse<Article> getArticlesCreatedBy(
            @PathVariable(value = "username") String username,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return userService.getUserArticles(username, page, size);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser){
        return userService.deleteUser(username, currentUser);
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User newUser, @PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser){
        return userService.updateUser(newUser, username, currentUser);
    }

    @PutMapping("/{username}/giveAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> giveAdmin(@PathVariable(name = "username") String username){
        return userService.giveAdmin(username);
    }

    @PutMapping("/{username}/takeAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> takeAdmin(@PathVariable(name = "username") String username){
        return userService.takeAdmin(username);
    }

    @PutMapping("/setOrUpdateInfo")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> setAddress(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody InfoRequest infoRequest){
        return userService.setOrUpdateInfo(currentUser, infoRequest);
    }

}
