package com.restblogv2.restblog.service;

import com.restblogv2.restblog.dto.CommentDto;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.comment.Comment;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.repository.CommentRepository;
import com.restblogv2.restblog.repository.UserRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;


    @Autowired
    public CommentService(CommentRepository commentRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createComment(CommentDto commentDto){

        Comment comment = new Comment();

        comment.setBody(commentDto.getBody());
        comment.setUser(userRepository.findById(commentDto.getUserId()).orElseThrow(()->new ResourceNotFoundException("User", "id", 1L)));
        comment.setArticle(articleRepository.findById(commentDto.getArticleId()).orElseThrow(()->new ResourceNotFoundException("Article", "id", 1L)));
        comment.setAuthorised(false);

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
    }

    public ResponseEntity<?> updateComment(
            Long id, CommentDto newCommentDto, UserPrincipal currentUser
            ){
        Comment comment = commentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Comment", "id", id));
        if (comment.getUser().getId().equals(currentUser.getId())){
            System.out.println("here");
            comment.setAuthorised(false);
            comment.setBody(newCommentDto.getBody());

            return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false,"Unauthorised"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteComment(
            Long id, UserPrincipal currentUser
    ){
        Comment comment = commentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Comment", "id", id));
        if (comment.getUser().getId().equals(currentUser.getId())){
            commentRepository.delete(comment);
            return new ResponseEntity<>("Comment deleted successfully!",HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false,"Unauthorised"), HttpStatus.UNAUTHORIZED);
    }

}
