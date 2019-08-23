package com.restblogv2.restblog.service;

import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.site.SiteConf;
import com.restblogv2.restblog.payload.SiteConfRequest;
import com.restblogv2.restblog.repository.SiteConfRepository;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SiteConfService {
    private final SiteConfRepository siteRepository;

    @Autowired
    public SiteConfService(SiteConfRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public ResponseEntity<?> addSiteConf(SiteConf siteConf){

        return new ResponseEntity<>(siteRepository.save(siteConf), HttpStatus.CREATED);
    }

    public ResponseEntity<?> getSiteConf(String key){

        SiteConf conf = siteRepository.findById(key).orElseThrow(() -> new ResourceNotFoundException("SiteConf", "key", key));

        return new ResponseEntity<>(conf, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllSiteConfs(int page, int size){
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        return new ResponseEntity<>(siteRepository.findAll(pageable), HttpStatus.OK);
    }

    public ResponseEntity<?> updateSiteConf(String key,SiteConf newConf){



        SiteConf conf = siteRepository.findById(key).orElseThrow(() -> new ResourceNotFoundException("SiteConf", "key", key));
        if (!newConf.getKey().isEmpty())
            conf.setKey(newConf.getKey());

        if (!newConf.getValue().isEmpty())
            conf.setValue(newConf.getValue());

        return new ResponseEntity<>(siteRepository.save(conf), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSiteConf(String key){

        SiteConf conf = siteRepository.findById(key).orElseThrow(() -> new ResourceNotFoundException("SiteConf", "key", key));
        siteRepository.delete(conf);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

}
