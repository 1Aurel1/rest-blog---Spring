package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.tag.Tag;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {
    Optional<Tag> findByTag(String tag);
}
