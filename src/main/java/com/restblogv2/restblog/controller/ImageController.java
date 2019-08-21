package com.restblogv2.restblog.controller;

import com.restblogv2.restblog.payload.ImageUpdate;
import com.restblogv2.restblog.security.CurrentUser;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.service.ImageService;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/images")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("")
    public ResponseEntity<?> getImages(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
        ){
        return imageService.getImageOfUser(currentUser.getId(), page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(
            @PathVariable("id") Long imageId,
            @CurrentUser UserPrincipal currentUser
        ){
        return imageService.getImage(imageId, currentUser);
    }

    @PostMapping("")
    public ResponseEntity<?> postImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("alt") String alt,
            @RequestParam("description") String description,
            @CurrentUser UserPrincipal currentUser
            ){

        return imageService.uploadImage(file, alt, description, currentUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(
            @PathVariable("id") Long id,
            @CurrentUser UserPrincipal currentUser
        ){

        return imageService.deleteImage(id, currentUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImage(
            @PathVariable("id") Long id,
            @Valid @RequestBody ImageUpdate imageUpdate,
            @CurrentUser UserPrincipal currentUser
        ){
        return imageService.updateImage(id, imageUpdate, currentUser);
    }

}
