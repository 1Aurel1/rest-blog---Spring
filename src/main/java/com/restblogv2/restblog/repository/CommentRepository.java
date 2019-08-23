package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
//    List<Comment> findAllByAr

    Page<Comment> findByAuthorisedIsFalse(Pageable pageable);

}
