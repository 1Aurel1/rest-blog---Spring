package com.restblogv2.restblog.model.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.audit.UserDateAudit;
import com.restblogv2.restblog.model.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image extends UserDateAudit implements Serializable {

    private static final long serialVersionUID = 1L;


    private final static String BASE_STORAGE_PATH = "/images/";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;

    private String alt;

    private String description;

    private String name;

    private String type;

    private long size;

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    @Getter(AccessLevel.NONE)
    private User user;

    @OneToMany(mappedBy = "featuredImage",
            cascade = CascadeType.DETACH,
            fetch = FetchType.LAZY
    )
    @Getter(AccessLevel.NONE)
    private List<Article> featuredInAtrilces;

    @ManyToMany(
            cascade = CascadeType.DETACH,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "article_image",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Getter(AccessLevel.NONE)
    private List<Article> articles;

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public List<Article> getArticles() {
        return articles;
    }

    @JsonIgnore
    public List<Article> getFeaturedInAtrilces() {
        return featuredInAtrilces;
    }

    public static String getBaseStoragePath() {
        return BASE_STORAGE_PATH;
    }
}
