package com.restblogv2.restblog.dto;

import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.category.Category;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotBlank
    @NotNull
    private String name;

    private Long parent;

    private List<Long> subCategories;

    private List<Long> articles;

}
