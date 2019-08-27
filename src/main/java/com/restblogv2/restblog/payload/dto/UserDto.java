package com.restblogv2.restblog.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class UserDto {

    private long id;

    @NotBlank
    @NotEmpty
    private String body;

    private String username;

}
