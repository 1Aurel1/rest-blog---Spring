package com.restblogv2.restblog.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private long id;

    @NotBlank
    @NotEmpty
    private String body;

    private Long articleId;

    private String username;

    @Setter(AccessLevel.NONE)
    private Long userId;

    @JsonIgnore
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
