package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.image.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    Page<Image> findAllByUserId(Long id, Pageable pageable);

    List<Image> findAllById(Iterable<Long> ids);

}
