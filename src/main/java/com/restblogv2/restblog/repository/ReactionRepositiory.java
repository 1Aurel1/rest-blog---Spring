package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.reactions.Reaction;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReactionRepositiory extends CrudRepository<Reaction, Long> {

    boolean existsReactionByUserIdAndArticleId(Long userId,Long  articleId);
    boolean existsReactionByUserIdAndArticleIdAndReaction(Long userId,Long  articleId, byte reaction);

}
