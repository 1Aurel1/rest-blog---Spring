package com.restblogv2.restblog.service;



import com.restblogv2.restblog.dto.ArticleDto;
import com.restblogv2.restblog.dto.CommentDto;
import com.restblogv2.restblog.exeption.AppException;
import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.comment.Comment;
import com.restblogv2.restblog.model.image.Image;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.model.tag.Tag;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.payload.PagedResponse;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.repository.ImageRepository;
import com.restblogv2.restblog.repository.TagRepository;
import com.restblogv2.restblog.repository.UserRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository, ImageRepository imageRepository, TagRepository tagRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.tagRepository = tagRepository;
    }

    public PagedResponse<Article> getAllArticlesShowalbe(int page, int size){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Article> articles = articleRepository.findAllByEnabledAndAuthorised(pageable);

        if (articles.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
        }

        return new PagedResponse<>(articles.getContent(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
    }

    public PagedResponse<Article> getArticlesCreatedBy(String username, int page, int size){
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Article> articles = articleRepository.findByCreatedBy(user.getId(), pageable);

        if(articles.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
        }
        return new PagedResponse<>(articles.getContent(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
    }

    public ResponseEntity<?> updateArticle(Long id, ArticleDto newArticle, UserPrincipal currentUser) throws Exception {
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
        if (article.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){

            article.setTitle(newArticle.getTitle());
            article.setBody(newArticle.getBody());
            article.setFeaturedImage(newArticle.getFeaturedImage());
            article.setSummary(newArticle.getSummary());

            System.err.println(newArticle.getTags());
            List<Tag> tags = new ArrayList<>();
            for (Long tag_id : newArticle.getTags()){
                tags.add(tagRepository.findById(tag_id).orElseThrow(() -> new AppException("Tag not found")));
            }
            article.setTags(tags);

            List<Image> images =  imageRepository.findAllById(newArticle.getImages());
            article.setImages(images);

            article.setSlug(newArticle.getSlug());
            article.setScheduledAt(newArticle.getScheduledAt());
            article.set_featured(newArticle.is_featured());
            article.setOpen_new_window(newArticle.isOpen_new_window());

            Article updatedArticle = articleRepository.save(article);
            return new ResponseEntity<>(updatedArticle, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to edit this article"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteArticle(Long id, UserPrincipal currentUser){
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
        if (article.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            articleRepository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted article"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(true, "You don't have permission to delete this article"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> addArticle(Article article, UserPrincipal currentUser){
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", 1L));

        if(article.getScheduledAt() == null)
            article.setEnabled(true);

        article.setUser(user);
        Article newArticle =  articleRepository.save(article);
        return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getArticle(Long id){
        Article article = articleRepository.getByIdAndEnabledIsTrueAndAuthorisedIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
        List<CommentDto> commentDtos = new ArrayList<>();

        for(Comment comment : article.getComments()){
            if (comment.isAuthorised()) {
                CommentDto commentDto = new CommentDto();
                commentDto.setId(comment.getId());
                commentDto.setBody(comment.getBody());
                commentDto.setUserId(comment.getUser().getId());
                commentDto.setUsername(comment.getUser().getUsername());
                commentDtos.add(commentDto);
            }
        }

        Map<String, Object> respose = new HashMap<>();


        respose.put("Article",article);
        respose.put("Comments",commentDtos);
        respose.put("Images", article.getImages());

        return new ResponseEntity<>(respose, HttpStatus.OK);
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

