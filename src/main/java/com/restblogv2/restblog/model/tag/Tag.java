package com.restblogv2.restblog.model.tag;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.audit.DateAudit;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity(name = "tags")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tag extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    @NotBlank
    @NotNull
    private String tag;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            }
    )
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Getter(AccessLevel.NONE)
    private List<Article> articles;


    @JsonIgnore
    public List<Article> getArticles() {
        return articles;
    }
}
