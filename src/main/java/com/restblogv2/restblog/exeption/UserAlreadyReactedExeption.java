package com.restblogv2.restblog.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class UserAlreadyReactedExeption extends RuntimeException {

    private Long user;
    private Long article;

    public UserAlreadyReactedExeption(Long user, Long article){
        super(String.format("User with id: '%s' already reacted to article with id: '%s'", user, article));
        this.user = user;
        this.article = article;
    }

}
