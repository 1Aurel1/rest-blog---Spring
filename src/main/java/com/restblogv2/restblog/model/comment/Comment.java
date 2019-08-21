package com.restblogv2.restblog.model.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.audit.UserDateAudit;
import com.restblogv2.restblog.model.user.User;
import com.restblogv2.restblog.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @NotEmpty
    private String body;

    private boolean authorised;

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            optional = false
    )
    @JoinColumn(name = "article_id")
    @Getter(AccessLevel.NONE)
    private Article article;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    },
            optional = false
    )
    @JoinColumn(name = "user_id")
    @Getter(AccessLevel.NONE)
    private User user;

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public Article getArticle() {
        return article;
    }
}
