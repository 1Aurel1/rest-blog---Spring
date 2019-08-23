package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.site.SiteConf;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SiteConfRepository extends PagingAndSortingRepository<SiteConf, String> {
}
