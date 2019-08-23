package com.restblogv2.restblog.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SiteConfRequest {
    @NotBlank
    @NotEmpty
    private String key;

    private String newKey;

    private String newValue;

}
