package com.restblogv2.restblog.service;

import com.restblogv2.restblog.payload.dto.ArticlePositionDto;
import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.comment.Comment;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.repository.CommentRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public AdminService(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    public ResponseEntity<?> getUnAuthorisedArticles(int page, int size, UserPrincipal currentUser){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            return new ResponseEntity<>(articleRepository.findAllByAuthorisedIsFalse(pageable), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "Unauthorised"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> authoriseArticles(Long article_id, boolean authorised, UserPrincipal currentUser){

        Article article = articleRepository.getById(article_id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", 1L));

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){

            article.setAuthorised(authorised);

            return new ResponseEntity<>(articleRepository.save(article), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "Unauthorised"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> updateArticlesPositions(List<ArticlePositionDto> articlePositionDtos, UserPrincipal currentUser){

        List<Long> ids = new ArrayList<>();
        articlePositionDtos.forEach((article) -> ids.add(article.getId()));
        List<Long> positions = new ArrayList<>();
        articlePositionDtos.forEach((article) -> positions.add(article.getPosition()));

        List<Article> articles = articleRepository.findArticlesById(ids);
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            for(int i = 0; i < articles.size(); i++){
                articles.get(i).setPosition(positions.get(i));
            }
            articleRepository.saveAll(articles);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to edit article position!"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> getAllUnAuthorisedComments(int page, int size){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        return new ResponseEntity<>(commentRepository.findByAuthorisedIsFalse(pageable), HttpStatus.OK);
    }

    public ResponseEntity<?> authoriseComment(Long id, boolean authorised){

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        comment.setAuthorised(authorised);
        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
    }



    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

}
