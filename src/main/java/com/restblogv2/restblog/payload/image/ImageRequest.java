package com.restblogv2.restblog.payload.image;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {

    private Long id;

    @Setter(AccessLevel.NONE)
    private String url;

    private String alt;

    private String description;

    @Getter(AccessLevel.NONE)
    private String childPath;

    @Getter(AccessLevel.NONE)
    private String fileName;

    public void setUrl(String url, String childPath, String fileName) {
        this.url = url + childPath + fileName;
    }
}
