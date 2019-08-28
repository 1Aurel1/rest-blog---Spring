package com.restblogv2.restblog.model.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restblogv2.restblog.model.article.Article;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @NotNull
    @Column(unique = true)
    private String name;

    private int position;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @Getter(AccessLevel.NONE)
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @Setter(AccessLevel.NONE)
    private List<Category> subCategories;

    @OneToMany(mappedBy = "category",
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            fetch = FetchType.LAZY
    )
    @Getter(AccessLevel.NONE)
    private List<Article> articles;

    @JsonIgnore
    public List<Article> getArticles() {
        return articles;
    }

    @JsonIgnore
    public Category getParent() {
        return parent;
    }

}
