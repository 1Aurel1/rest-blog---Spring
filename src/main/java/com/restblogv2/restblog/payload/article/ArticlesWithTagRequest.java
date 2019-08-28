package com.restblogv2.restblog.payload.article;

import com.restblogv2.restblog.model.article.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesWithTagRequest {

    private String tag;

    private List<Article> articles;

}
