package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.reactions.Reaction;
import org.springframework.data.repository.CrudRepository;

public interface ReactionRepositiory extends CrudRepository<Reaction, Long> {
}
