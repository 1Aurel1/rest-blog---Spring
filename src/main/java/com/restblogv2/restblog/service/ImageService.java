package com.restblogv2.restblog.service;

import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.image.Image;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.payload.ImageUpdate;
import com.restblogv2.restblog.repository.ImageRepository;
import com.restblogv2.restblog.repository.UserRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.util.AppConstants;
import com.restblogv2.restblog.util.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService extends FileStorage {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getImageOfUser(Long userId, int page, int size){

        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        return new ResponseEntity<>(imageRepository.findAllByUserId(userId, pageable), HttpStatus.OK);
    }

    public ResponseEntity<?> uploadImage(MultipartFile file, String alt, String description, UserPrincipal currentUser){

        Image image = new Image();

        image.setAlt(alt);
        image.setDescription(description);

        image.setName(file.getOriginalFilename());
        image.setType(file.getContentType());
        image.setSize(file.getSize());
        image.setUser(userRepository.findById(currentUser.getId()).orElseThrow(()-> new ResourceNotFoundException("User", "id", currentUser.getId())));

        image = imageRepository.save(image);
        String childPath = Image.getBaseStoragePath() + image.getId();

        image.setUrl(storeFile(file, childPath));

        return new ResponseEntity<>(imageRepository.save(image), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteImage(Long imageId, UserPrincipal currentUser){
        Image image = imageRepository.findById(imageId).orElseThrow(()-> new ResourceNotFoundException("Image", "id", imageId));

        if (image.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            if (deleteFile(image.getUrl(), image.getType())){
                imageRepository.delete(image);
                return new ResponseEntity<>( new ApiResponse(true, "Image deleted succesfully"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Faild deleting image!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ApiResponse(false, "You are not authorised to delte this image!"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> getImage(
            Long imageId, UserPrincipal currentUser
        ){
        Image image = imageRepository.findById(imageId).orElseThrow(()-> new ResourceNotFoundException("Image", "id", imageId));

        if (image.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            return new ResponseEntity<>(image, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You are not authorised to delte this image!"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> updateImage(
            Long id, ImageUpdate newImage, UserPrincipal currentUser
        ){
        Image image = imageRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Image", "Id", id));
        if (image.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){

            image.setAlt(newImage.getAlt());
            image.setDescription(newImage.getDescription());

            return new ResponseEntity<>(imageRepository.save(image), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You are not authorised"), HttpStatus.UNAUTHORIZED);
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
