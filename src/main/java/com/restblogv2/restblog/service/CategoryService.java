package com.restblogv2.restblog.service;

import com.restblogv2.restblog.dto.CategoryDto;
import com.restblogv2.restblog.exeption.AppException;
import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.category.Category;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.repository.CategoryRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ArticleRepository articleRepository;
    
    private final UserRepository userRepository;


    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getAllCategories(){
        return new ResponseEntity<>(categoryRepository.findAllByParentNull(), HttpStatus.OK);
    }

    public ResponseEntity<?> addCategory(CategoryDto categoryDto){

        Category newCategory = new Category();
//
        newCategory.setName(categoryDto.getName());

        if (categoryDto.getParent() != null){
            newCategory.setParent(categoryRepository.findById(categoryDto.getParent()).orElseThrow(() -> new ResourceNotFoundException("Category", "id", 1L)));
        }

        if (categoryDto.getArticles() != null ){
            List<Article> articles = articleRepository.findAllById(categoryDto.getArticles());
            newCategory.setArticles(articles);
        }

        newCategory = categoryRepository.save(newCategory);

        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getCategoryArticles(Long id, int page, int size){

        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Article> articles = articleRepository.findArticlesByCategory(id, pageable);

        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    public ResponseEntity<?> updateCategory(Long id, CategoryDto updatedCategory, UserPrincipal currentUser){

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){

            Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", 1L));

            category.setName(updatedCategory.getName());
            if (updatedCategory.getParent() != null){
                category.setParent(categoryRepository.findById(updatedCategory.getParent()).orElseThrow(() -> new ResourceNotFoundException("Category", "id", 1L)));
            }
            if (updatedCategory.getArticles() != null ){
                List<Article> articles = articleRepository.findArticlesById(updatedCategory.getArticles());
                for (Article article : articles){
                    article.setCategory(category);
                }
                articleRepository.saveAll(articles);
//                List<Article> articles = articleRepository.findArticlesById(updatedCategory.getArticles());
//                category.setArticles(articles);
//                System.out.println(articles.get(0).getId());
            }

            return new ResponseEntity<>(categoryRepository.save(category), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(false, "You are not authorised to take this action") , HttpStatus.UNAUTHORIZED);
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
