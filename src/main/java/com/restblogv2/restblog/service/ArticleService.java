package com.restblogv2.restblog.service;



import com.restblogv2.restblog.payload.ArticelImagesRelations;
import com.restblogv2.restblog.payload.article.ArticleRequest;
import com.restblogv2.restblog.payload.dto.CommentRequest;
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
import com.restblogv2.restblog.repository.*;
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
    private final CategoryRepository categoryRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository, ImageRepository imageRepository, TagRepository tagRepository, CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponseEntity<?> getAllArticlesShowalbe(int page, int size){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Article> articles = articleRepository.findAllByEnabledAndAuthorised(pageable);


        if (articles.getNumberOfElements() == 0){
            PagedResponse<Article> pagedResponse = new PagedResponse<>(Collections.emptyList(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
            return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
        }

        PagedResponse<Article> articlePagedResponse= new PagedResponse<>(articles.getContent(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
        return new ResponseEntity<>(articlePagedResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getArticlesCreatedBy(String username, int page, int size){
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Article> articles = articleRepository.findByCreatedBy(user.getId(), pageable);

        if(articles.getNumberOfElements() == 0){
            PagedResponse<Article> articlePagedResponse = new PagedResponse<>(Collections.emptyList(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
            return new ResponseEntity<>(articlePagedResponse, HttpStatus.OK);
        }
        PagedResponse<Article> articlePagedResponse = new PagedResponse<>(articles.getContent(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
        return new ResponseEntity<>(articlePagedResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> updateArticle(Long id, ArticleRequest newArticle, UserPrincipal currentUser) throws Exception {
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
        if (article.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){

            article.setTitle(newArticle.getTitle());
            article.setBody(newArticle.getBody());
            article.setFeaturedImage(imageRepository.findById(newArticle.getFeaturedImage()).orElseThrow(()->new ResourceNotFoundException("Image", "id", newArticle.getFeaturedImage())));
            article.setSummary(newArticle.getSummary());

            if(newArticle.getCategory()!=null)
                article.setCategory(categoryRepository.findById(newArticle.getCategory()).orElseThrow(()->new ResourceNotFoundException("Category","id",newArticle.getCategory())));

            System.err.println(newArticle.getTags());
            List<Tag> tags = new ArrayList<>();
            if (!newArticle.getTags().isEmpty())
                for (Long tag_id : newArticle.getTags()){
                    tags.add(tagRepository.findById(tag_id).orElseThrow(() -> new AppException("Tag not found")));
                }
            article.setTags(tags);

            List<Image> images = imageRepository.findAllById(newArticle.getImages());
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

    public ResponseEntity<?> addArticle(ArticleRequest newArticle, UserPrincipal currentUser){
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", 1L));

        Article article = new Article();

        if(newArticle.getScheduledAt() == null)
            article.setEnabled(true);
        else
            article.setScheduledAt(newArticle.getScheduledAt());
        article.setUser(user);

        article.setTitle(newArticle.getTitle());
        article.setBody(newArticle.getBody());
        article.setFeaturedImage(imageRepository.findById(newArticle.getFeaturedImage()).orElseThrow(()->new ResourceNotFoundException("Image", "id", newArticle.getFeaturedImage())));
        article.setSummary(newArticle.getSummary());

        if(newArticle.getCategory()!=null)
            article.setCategory(categoryRepository.findById(newArticle.getCategory()).orElseThrow(()->new ResourceNotFoundException("Category","id",newArticle.getCategory())));

        if (newArticle.getTags()!=null) {
            List<Tag> tags = new ArrayList<>();
             if(!newArticle.getTags().isEmpty()) {
                 for (Long tag_id : newArticle.getTags()) {
                     tags.add(tagRepository.findById(tag_id).orElseThrow(() -> new AppException("Tag not found")));
                 }
             }
            article.setTags(tags);
        }else {
            article.setTags(new ArrayList<>());
        }

        if(newArticle.getImages() != null) {
            List<Image> images = imageRepository.findAllById(newArticle.getImages());
            article.setImages(images);
        }else {
            article.setImages(new ArrayList<>());
        }

        article.setSlug(newArticle.getSlug());
        article.setScheduledAt(newArticle.getScheduledAt());
        article.set_featured(newArticle.is_featured());
        article.setOpen_new_window(newArticle.isOpen_new_window());

        article = articleRepository.save(article);

        return new ResponseEntity<>(article, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getArticle(Long id){
        Article article = articleRepository.getByIdAndEnabledIsTrueAndAuthorisedIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
        List<CommentRequest> commentDtos = new ArrayList<>();

        for(Comment comment : article.getComments()){
            if (comment.isAuthorised()) {
                CommentRequest commentDto = new CommentRequest();
                commentDto.setId(comment.getId());
                commentDto.setBody(comment.getBody());
                commentDto.setUserId(comment.getUser().getId());
                commentDto.setUsername(comment.getUser().getUsername());
                commentDtos.add(commentDto);
            }
        }

        Map<String, Object> respose = new HashMap<>();

        List<Image> images = article.getImages();
        for (Image image : images){
            image.setUrl(image.getUrl() + AppConstants.DEFAULT_IMAGE_SIZE + image.getName());
        }

        respose.put("Article",article);
        respose.put("Comments",commentDtos);
        respose.put("Images", images);

        return new ResponseEntity<>(respose, HttpStatus.OK);
    }

    public ResponseEntity<?> createImageRelations(Long articleId, ArticelImagesRelations articelImagesRelations, UserPrincipal currentUser) {

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        if (article.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            List<Image> currentImages = article.getImages();
            List<Image> images = imageRepository.findAllById(articelImagesRelations.getImagesIds());

            if (currentImages == null){
                article.setImages(images);
            }else{
                for (Image image : images){
                    boolean exists = false;
                    for (Image image1 : currentImages)
                        if (image.getId() == image1.getId())
                            exists = true;
                    if (!exists)
                        currentImages.add(image);
                }
            }

            article.setImages(currentImages);

            return new ResponseEntity<>(articleRepository.save(article), HttpStatus.OK);

        }

        return new ResponseEntity<>(new ApiResponse(false, "You are not authorised!"), HttpStatus.UNAUTHORIZED);
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

