package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.article.ArticleMeta;
import org.springframework.data.repository.CrudRepository;

public interface ArticleMetaRepositiory extends CrudRepository<ArticleMeta, Integer> {
}
