package com.restblogv2.restblog.payload.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class UserRequest {

    private long id;

    @NotBlank
    @NotEmpty
    private String body;

    private String username;

}
