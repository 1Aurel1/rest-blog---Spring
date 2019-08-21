package com.restblogv2.restblog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.model.article.Article;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


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
