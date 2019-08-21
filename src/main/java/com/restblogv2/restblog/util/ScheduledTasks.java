package com.restblogv2.restblog.util;

import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private final ArticleRepository articleRepository;

    @Autowired
    public ScheduledTasks(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    @Scheduled(fixedRate = 500000)
    public void checkForScheduledArticles() {
        List<Article> articles = articleRepository.findAllByEnabledIsFalse();
        for (Article article : articles){
            if (article.getScheduledAt().after(new Date())){
                article.setEnabled(true);
            }
        }
    }

}
