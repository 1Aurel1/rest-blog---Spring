package com.restblogv2.restblog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.user.User;
import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserDto {

    private long id;

    @NotBlank
    @NotEmpty
    private String body;

    private String username;

}
