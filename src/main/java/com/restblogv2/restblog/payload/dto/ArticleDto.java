package com.restblogv2.restblog.payload.dto;

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
public class ArticleDto extends DateAudit {

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

    private Image featuredImage;

    private long position;

    private String scheduledAt;

    private boolean authorised;

    private boolean enabled;

    private boolean is_featured;

    private boolean open_new_window;

//    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL)
//    private ArticleMeta meta;


    private Long user;

//    @ManyToOne(cascade = {
//            CascadeType.DETACH, CascadeType.MERGE,
//            CascadeType.PERSIST, CascadeType.REFRESH
//    }, optional = true)
//    @JoinColumn(name = "category_id")
//    private Category category;

//    @OneToMany(mappedBy = "article",
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY
//    )
//    private List<ArticleComment> comments;
//
//    @OneToMany(mappedBy = "article",
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY
//    )
//    private List<Reaction> reactions;


    private List<Long> tags;


   private List<Long> images;


}
