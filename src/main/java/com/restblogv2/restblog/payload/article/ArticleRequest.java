package com.restblogv2.restblog.payload.article;

import com.restblogv2.restblog.model.audit.DateAudit;
import com.restblogv2.restblog.model.image.Image;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleRequest extends DateAudit {

    @NotBlank
    @NotNull
    private String title;

    @Lob
    @NotBlank
    @NotNull
    private String body;

    @NotBlank
    @NotNull
    private String summary;

    @Column(unique = true)
    @NotBlank
    @NotNull
    private String slug;

    private Long featuredImage;

    private long position;

    private String scheduledAt;

    private boolean authorised;

    private boolean enabled;

    private boolean is_featured;

    private boolean open_new_window;

    private Long user;

    private Long category;


    private List<Long> tags;


   private Iterable<Long> images;


}
