package com.restblogv2.restblog.payload.auth;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.internal.bytebuddy.agent.builder.AgentBuilder;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class InfoRequest {
    @NotBlank
    private String street;

    @NotBlank
    private String suite;

    @NotBlank
    private String city;

    @NotBlank
    private String zipcode;

    private String companyName;

    private String catchPhrase;

    private String bs;

    private String website;

    private String phone;

    private String lat;

    private String lng;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}
