package com.restblogv2.restblog.payload.user;

import com.restblogv2.restblog.model.user.Address;
import com.restblogv2.restblog.model.user.Company;
import com.restblogv2.restblog.validation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Instant joinedAt;
    private String email;
    private Address address;
    @PhoneNumber
    private String phone;
    private String website;
    private Company company;
    private Long postCount;

}