package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.user.UserResetPasswordToken;
import com.restblogv2.restblog.model.user.UserVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserResetTokenRepository extends CrudRepository<UserResetPasswordToken, Long> {
    Optional<UserResetPasswordToken> findByConfirmationTokenAndEnabledIsTrue(String token);
}
