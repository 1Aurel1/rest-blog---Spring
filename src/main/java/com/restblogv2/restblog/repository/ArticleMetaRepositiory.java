package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.article.ArticleMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleMetaRepositiory extends CrudRepository<ArticleMeta, Integer> {

    List<ArticleMeta> findAllByArticleId(Long id);



}
