package com.restblogv2.restblog.controller;


import com.restblogv2.restblog.exeption.AppException;
import com.restblogv2.restblog.model.role.Role;
import com.restblogv2.restblog.model.role.RoleName;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.model.user.UserResetPasswordToken;
import com.restblogv2.restblog.model.user.UserVerificationToken;
import com.restblogv2.restblog.payload.ApiResponse;
import com.restblogv2.restblog.payload.ResetPasswordRequest;
import com.restblogv2.restblog.payload.auth.JwtAuthenticationResponse;
import com.restblogv2.restblog.payload.auth.LoginRequest;
import com.restblogv2.restblog.payload.auth.SignUpRequest;
import com.restblogv2.restblog.repository.RoleRepository;
import com.restblogv2.restblog.repository.UserRepository;
import com.restblogv2.restblog.repository.UserResetTokenRepository;
import com.restblogv2.restblog.repository.UserVerificationRepository;
import com.restblogv2.restblog.security.JwtTokenProvider;
import com.restblogv2.restblog.service.AuthService;
import com.restblogv2.restblog.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        return authService.registerUser(signUpRequest);
    }

    @RequestMapping(value = "/confirm-email", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        return authService.confirmAccount(confirmationToken);
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<?> sendRestPaswordToken(
               @RequestParam(name = "email") String email
        ){
        return authService.sendResetToken(email);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest
            ){

        if (resetPasswordRequest.getPass().equals(resetPasswordRequest.getConfPass()))
            return authService.resetUserPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getPass());
        return new ResponseEntity<>(new ApiResponse(false, "Passwords did not match!"), HttpStatus.BAD_REQUEST);
    }

}
