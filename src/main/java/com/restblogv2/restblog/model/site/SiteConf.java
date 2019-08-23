package com.restblogv2.restblog.model.site;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="site_settings", uniqueConstraints = {@UniqueConstraint(columnNames = "conf_key")})
public class SiteConf implements Serializable {

    @Id
    @Column(name = "conf_key", length = 64)
    private String key;

    @NotBlank
    @NotEmpty
    @Column(name = "conf_value",  length = 64)
    private String value;

}
