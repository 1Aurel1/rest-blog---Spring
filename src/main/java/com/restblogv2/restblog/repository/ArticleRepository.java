package com.restblogv2.restblog.repository;


import com.restblogv2.restblog.model.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {


    Optional<Article> getById(Long id);
    Optional<Article> getByIdAndEnabledIsTrueAndAuthorisedIsTrue(Long id);

    List<Article> findArticlesById(List<Long> id);
    List<Article> findAllByEnabledIsFalse();

    Page<Article> findByCreatedBy(Long userId, Pageable pageable);
    @Query("select a from #{#entityName} a where a.enabled = true and a.authorised = true")
    Page<Article> findAllByEnabledAndAuthorised(Pageable pageable);
    @Query("select a from #{#entityName} a where a.enabled = true and a.authorised = true and a.category.id = :id")
    Page<Article> findArticlesByCategory(@Param("id") Long category_id, Pageable pageable);
    Page<Article> findAllByAuthorisedIsFalse(Pageable pageable);


}
