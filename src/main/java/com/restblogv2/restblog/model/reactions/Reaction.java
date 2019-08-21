package com.restblogv2.restblog.model.reactions;

import com.restblogv2.restblog.model.article.Article;
import com.restblogv2.restblog.model.audit.UserDateAudit;
import com.restblogv2.restblog.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reaction extends UserDateAudit {

    private final static String[] REACTIONS = {"Like", "Love", "Haha", "Yay", "Wow", "Sad", "Angry"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter(AccessLevel.NONE)
    private byte reaction;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "article_id")
    @Getter(AccessLevel.NONE)
    private Article article;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id")
    @Getter(AccessLevel.NONE)
    private User user;

    public String getReaction() {
        switch (reaction) {
            case 0: {
                return "Undefinded";
            }
            case 1: {
                return "Like";
            }
            case 2: {
                return "Love";
            }
            case 3: {
                return "Haha";
            }
            case 4: {
                return "Yay";
            }
            case 5: {
                return "Wow";
            }
            case 6: {
                return "Sad";
            }
            case 7: {
                return "Angry";
            }
            default: {
                return "";
            }
        }
    }

    public Map<String, Object> getUser() {
        Map<String, Object> userMap = new HashMap<>();
        long uId = user.getId();
        userMap.put("id", uId);
        userMap.put("username", user.getUsername());

        return userMap;
    }
}
