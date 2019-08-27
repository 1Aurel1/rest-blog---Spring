package com.restblogv2.restblog.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.audit.DateAudit;
import com.restblogv2.restblog.model.comment.Comment;
import com.restblogv2.restblog.model.reactions.Reaction;
import com.restblogv2.restblog.model.role.Role;
import com.restblogv2.restblog.validation.PhoneNumber;
import com.sun.org.apache.regexp.internal.RE;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "first_name")
    @Size(max = 40)
    @Pattern(regexp = "^[a-zA-Z]+([',. -])*$",message = "Please enter a valid firsname")
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    @Size(max = 40)
    @Pattern(regexp = "^[a-zA-Z]+([',. -])*$", message = "Please enter a valid surname")
    private String lastName;

    @NotBlank
    @Column(name = "username")
    @Size(max = 15)
    @Pattern(regexp = "^[a-z0-9_-]{3,15}$", message = "Username is invalid")
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 100)
    @Column(name = "password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,10}$", message = "Password must contain a capital letter, a small letter, a number and a special caracter!")
    private String password;

    @NotBlank
    @NaturalId
    @Size(max = 40)
    @Column(name = "email")
    @Email
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;


    @Column(name = "phone")
    @Pattern(regexp = "^[0-9]{10}", message = "Required 10 numbers")
    private String phone;

    @Column(name = "website")
    @Pattern(regexp = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$")
    private String website;


    @Column
    private boolean enabled;

    @Column
    private boolean accountNonExpired;

    @Column
    private boolean credentialsNonExpired;

    @Column
    private boolean accountNonLocked;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "company_id")
    private Company company;



    @OneToMany(
            mappedBy = "createdBy",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            }
    )
    @Getter(AccessLevel.NONE)
    private List<Article> articles;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Getter(AccessLevel.NONE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Getter(AccessLevel.NONE)
    private List<Reaction> reactions;


    public User(String firstName, String lastName, String username, String email, String password, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @JsonIgnore
    public List<Article> getArticles() {
        return articles;
    }

    @JsonIgnore
    public List<Comment> getComments() {
        return comments;
    }

    @JsonIgnore
    public List<Reaction> getReactions() {
        return reactions;
    }
}