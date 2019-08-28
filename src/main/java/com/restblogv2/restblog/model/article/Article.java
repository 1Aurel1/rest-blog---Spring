package com.restblogv2.restblog.model.article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.exeption.AppException;
import com.restblogv2.restblog.model.audit.UserDateAudit;
import com.restblogv2.restblog.model.category.Category;
import com.restblogv2.restblog.model.comment.Comment;
import com.restblogv2.restblog.model.image.Image;
import com.restblogv2.restblog.model.reactions.Reaction;
import com.restblogv2.restblog.model.tag.Tag;
import com.restblogv2.restblog.model.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "articles")
@NoArgsConstructor
@Getter
@Setter
public class Article extends UserDateAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
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

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int reactionCount;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "image_id")
    private Image featuredImage;

    private long position;

    @Temporal(TemporalType.DATE)
    @Setter(AccessLevel.NONE)
    private Date scheduledAt;

    @Getter(AccessLevel.NONE)
    private boolean authorised;

    @Getter(AccessLevel.NONE)
    private boolean enabled;

    @Getter(AccessLevel.NONE)
    private boolean is_featured;

    private boolean open_new_window;

    @OneToMany(mappedBy = "article",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<ArticleMeta> metas;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    @Getter(AccessLevel.NONE)
    private User user;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "category_id")
    @Getter(AccessLevel.NONE)
    private Category category;

    @OneToMany(mappedBy = "article",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Getter(AccessLevel.NONE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "article",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Getter(AccessLevel.NONE)
    private List<Reaction> reactions;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            }
    )
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    @Getter(AccessLevel.NONE)
    private List<Tag> tags;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.DETACH
    )
    @JoinTable(
            name = "article_image",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    @Getter(AccessLevel.NONE)
    private List<Image> images;


    public Article(@NotBlank @NotNull String title, @NotBlank @NotNull String body, @NotBlank @NotNull String summary, @NotBlank @NotNull String slug, Image featuredImage, long position, String scheduledAt, boolean authorised, boolean enabled, boolean is_featured, boolean open_new_window, User user, List<Tag> tags) {
        this.title = title;
        this.body = body;
        this.summary = summary;
        this.slug = slug;
        this.featuredImage = featuredImage;
        this.position = position;
        try {
            this.scheduledAt = dateFormat.parse(scheduledAt);
        } catch (ParseException e) {
            throw new AppException("Enter a valid date format");
        }
        this.authorised = authorised;
        this.enabled = enabled;
        this.is_featured = is_featured;
        this.open_new_window = open_new_window;
        this.user = user;
        this.tags = tags;
    }




    @JsonIgnore
    public List<Comment> getComments() {
        return comments;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public List<Tag> getTags() {
        return tags;
    }

    public void setScheduledAt(String scheduledAt) {
        if (scheduledAt!=null)
            try {
                this.scheduledAt = dateFormat.parse(scheduledAt);
            } catch (ParseException e) {
                throw new AppException("Enter a valid date format");
            }
    }

    @JsonIgnore
    public List<Image> getImages() {
        return images;
    }

    @JsonIgnore
    public Category getCategory() {
        return category;
    }

    @JsonIgnore
    public boolean isAuthorised() {
        return authorised;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    public int getReactionCount() {
        if(reactions == null)
            return 0;
        return reactions.size();
    }

}

