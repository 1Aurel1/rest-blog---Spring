package com.restblogv2.restblog.payload.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank
    @NotNull
    private String name;

    private Long parent;

    private List<Long> subCategories;

    private Iterable<Long> articles;

}
