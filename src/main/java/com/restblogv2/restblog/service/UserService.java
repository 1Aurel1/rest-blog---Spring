package com.restblogv2.restblog.service;

import com.restblogv2.restblog.exeption.AppException;
import com.restblogv2.restblog.exeption.BadRequestException;
import com.restblogv2.restblog.exeption.ResourceNotFoundException;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.role.Role;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.model.user.Address;
import com.restblogv2.restblog.model.user.Company;
import com.restblogv2.restblog.model.user.Geo;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.payload.*;
import com.restblogv2.restblog.payload.auth.InfoRequest;
import com.restblogv2.restblog.payload.user.UserIdentityAvailability;
import com.restblogv2.restblog.payload.user.UserProfile;
import com.restblogv2.restblog.payload.user.UserSummary;
import com.restblogv2.restblog.repository.ArticleRepository;
import com.restblogv2.restblog.repository.RoleRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ArticleRepository articleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.articleRepository = articleRepository;
    }

    public UserSummary getCurrentUser(UserPrincipal currentUser){
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(), currentUser.getLastName());
    }

    public UserIdentityAvailability checkUsernameAvailability(String username){
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    public UserIdentityAvailability checkEmailAvailability(String email){
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    public UserProfile getUserProfile(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

//        Long postCount = postRepository.countByCreatedBy(user.getId());

        return new UserProfile(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getCreatedAt(), user.getEmail(), user.getAddress(), user.getPhone(), user.getWebsite(), user.getCompany(), (long) 0);
    }

    public ResponseEntity<?> addUser(User user){
        if(userRepository.existsByUsername(user.getUsername())){
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(user.getEmail())){
            return new ResponseEntity<>(new ApiResponse(false, "Email is already taken"), HttpStatus.BAD_REQUEST);
        }

        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User result =  userRepository.save(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateUser(User newUser, String username, UserPrincipal currentUser){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if(user.getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user.setAddress(newUser.getAddress());
            user.setPhone(newUser.getPhone());
            user.setWebsite(newUser.getWebsite());
            user.setCompany(newUser.getCompany());

            User updatedUser =  userRepository.save(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);

        }

        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to update profile of: " + username), HttpStatus.UNAUTHORIZED);

    }

    public ResponseEntity<?> deleteUser(String username, UserPrincipal currentUser){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
        if(!user.getId().equals(currentUser.getId())){
            return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to delete profile of: " + username), HttpStatus.UNAUTHORIZED);
        }
        userRepository.deleteById(user.getId());

        return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted profile of: " + username), HttpStatus.OK);
    }

    public ResponseEntity<?> giveAdmin(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set")));
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "You gave ADMIN role to user: " + username), HttpStatus.OK);
    }

    public ResponseEntity<?> takeAdmin(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "You took ADMIN role from user: " + username), HttpStatus.OK);
    }

    public ResponseEntity<?> setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest) {
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
        Geo geo = new Geo(infoRequest.getLat(), infoRequest.getLng());
        Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(), infoRequest.getZipcode(), geo);
        Company company = new Company(infoRequest.getCompanyName(), infoRequest.getCatchPhrase(), infoRequest.getBs());
        if (user.getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            user.setAddress(address);
            user.setCompany(company);
            user.setWebsite(infoRequest.getWebsite());
            user.setPhone(infoRequest.getPhone());
            User updatedUser = userRepository.save(user);

//            Long postCount = postRepository.countByCreatedBy(updatedUser.getId());


            UserProfile userProfile = new UserProfile(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getCreatedAt(), updatedUser.getEmail(), updatedUser.getAddress(), updatedUser.getPhone(), updatedUser.getWebsite(), updatedUser.getCompany(), (long) 0);
            return new ResponseEntity<>(userProfile, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to update users profile"), HttpStatus.OK);
    }
    public PagedResponse<Article> getUserArticles(String username, Integer page, Integer size) {
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Article> articles = articleRepository.findByCreatedBy(user.getId(), pageable);

        if(articles.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
        }
        return new PagedResponse<>(articles.getContent(), articles.getNumber(), articles.getSize(), articles.getTotalElements(), articles.getTotalPages(), articles.isLast());
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
