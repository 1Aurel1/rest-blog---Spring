package com.restblogv2.restblog.service;

import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.model.tag.Tag;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.payload.PagedResponse;
import com.restblogv2.restblog.repository.TagRepository;
import com.restblogv2.restblog.repository.UserRepository;
import com.restblogv2.restblog.security.UserPrincipal;
import com.restblogv2.restblog.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public TagService(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    public PagedResponse<Tag> getAllTags(int page, int size){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Tag> tags = tagRepository.findAll(pageable);

        if (tags.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), tags.getNumber(), tags.getSize(), tags.getTotalElements(), tags.getTotalPages(), tags.isLast());
        }

        return new PagedResponse<>(tags.getContent(), tags.getNumber(), tags.getSize(), tags.getTotalElements(), tags.getTotalPages(), tags.isLast());
    }

    public ResponseEntity<?> deleteTag(Long id, UserPrincipal currentUser){
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            tagRepository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted tag"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(true, "You don't have permission to delete this tag"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> addTag(Tag tag, UserPrincipal currentUser){
        if (userRepository.existsById(currentUser.getId())){
            Tag newTag =  tagRepository.save(tag);
            return new ResponseEntity<>(newTag, HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(new ApiResponse(true, "You have to be authenticated to create a tag"), HttpStatus.UNAUTHORIZED);
        }

    }

    public ResponseEntity<?> getArticlesWithTag(
            String id
        ){

        Tag tag = tagRepository.findByTag(id).orElseThrow(()->new ResourceNotFoundException("Tag", "id", id));


        Map<String, Object> result = new HashMap<>();
        result.put("Tag", tag);
        result.put("Articles", tag.getArticles());

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    public ResponseEntity<?> exists(String tag){
        return new ResponseEntity<>(tagRepository.existsTagByTag(tag), HttpStatus.OK);
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

    public ResponseEntity<?> getTag(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }
}
