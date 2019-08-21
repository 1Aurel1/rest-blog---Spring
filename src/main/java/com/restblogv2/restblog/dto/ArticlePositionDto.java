package com.restblogv2.restblog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticlePositionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long position;

}
