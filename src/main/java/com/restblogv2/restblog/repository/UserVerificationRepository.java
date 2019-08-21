package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.user.UserVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepository extends JpaRepository<UserVerificationToken, Long> {
    UserVerificationToken findByConfirmationToken(String confirmationToken);
}
