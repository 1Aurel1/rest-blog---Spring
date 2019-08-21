package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.category.Category;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    List<Category> findAllByParentNull();

}
