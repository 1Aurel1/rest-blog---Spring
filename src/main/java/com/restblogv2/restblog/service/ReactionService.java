package com.restblogv2.restblog.service;

import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.exeption.UserAlreadyReactedExeption;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.reactions.Reaction;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.payload.reaction.ReactionRequest;
import com.restblogv2.restblog.payload.reaction.UpdateReactionRequest;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.repository.ReactionRepositiory;
import com.restblogv2.restblog.repository.UserRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class ReactionService {

    private final ReactionRepositiory reactionRepositiory;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReactionService(ReactionRepositiory reactionRepositiory, ArticleRepository articleRepository, UserRepository userRepository) {
        this.reactionRepositiory = reactionRepositiory;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }


    public ResponseEntity<?> createReaction(
            Long userId, ReactionRequest reactionRequest
        ){

        if (reactionRepositiory.existsReactionByUserIdAndArticleId(userId, reactionRequest.getArticleId()))
            throw new UserAlreadyReactedExeption(userId, reactionRequest.getArticleId());

        Article article = articleRepository.getById(reactionRequest.getArticleId()).orElseThrow(() -> new ResourceNotFoundException("Article", "id", reactionRequest.getArticleId()));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Reaction reaction = new Reaction();

        reaction.setReaction(reactionRequest.getReaction());
        reaction.setArticle(article);
        reaction.setUser(user);

        return new ResponseEntity<>(reactionRepositiory.save(reaction), HttpStatus.OK);
    }

    public ResponseEntity<?> updateReaction(UserPrincipal currentUser, Long reactionId, UpdateReactionRequest updateReactionRequest){

        Reaction reaction = reactionRepositiory.findById(reactionId).orElseThrow(() -> new ResourceNotFoundException("Reaction", "id", reactionId));

        if (reaction.getUser().get("id").equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {

            reaction.setReaction(updateReactionRequest.getReaction());

            return new ResponseEntity<>(reactionRepositiory.save(reaction), HttpStatus.OK);

        }
        return  new ResponseEntity<>(new ApiResponse(false, "YOu are not authorised"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteReaction(UserPrincipal currentUser, Long reactionId) {
        Reaction reaction = reactionRepositiory.findById(reactionId).orElseThrow(() -> new ResourceNotFoundException("Reaction", "id", reactionId));

        if (reaction.getUser().get("id").equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {

            reactionRepositiory.delete(reaction);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        return new ResponseEntity<>(new ApiResponse(false, "You are not authorised"), HttpStatus.UNAUTHORIZED);
    }
}
