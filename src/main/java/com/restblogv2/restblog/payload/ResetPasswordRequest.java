package com.restblogv2.restblog.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotNull
    @NotBlank
    private String token;
    @NotNull
    @NotBlank
    private String pass;
    @NotNull
    @NotBlank
    private String confPass;
}
