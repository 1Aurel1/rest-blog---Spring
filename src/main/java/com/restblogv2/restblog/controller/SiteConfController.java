package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.model.site.SiteConf;
import com.restblogv2.restblog.service.SiteConfService;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/siteConf")
@PreAuthorize("hasRole('ADMIN')")
public class SiteConfController {

    private final SiteConfService siteConfService;

    @Autowired
    public SiteConfController(SiteConfService siteConfService) {
        this.siteConfService = siteConfService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllSiteConf(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
    ){

        return siteConfService.getAllSiteConfs(page, size);
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getSeteConf(@PathVariable("key") String key){
        return siteConfService.getSiteConf(key);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> deleteSiteConf(@PathVariable("key") String key){
        return siteConfService.deleteSiteConf(key);
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> updateSiteConf(
            @PathVariable("key") String key,
            @RequestBody SiteConf newSiteConf
            ){
        return siteConfService.updateSiteConf(key, newSiteConf);
    }

}
