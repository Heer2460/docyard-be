package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "FORGOT_PASSWORD_LINKS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPasswordLink extends BaseEntity {

    @Column(name = "EXPIRED_ON")
    private ZonedDateTime expiredOn;
    @Column(name = "IS_EXPIRED")
    private Boolean expired;
    @Column(name = "TOKEN")
    private String token;
    public ForgotPasswordLink() {
    }

    public ForgotPasswordLink(String token) {
        this.setCreatedBy(1L);
        this.setUpdatedBy(1L);
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
        this.expiredOn = ZonedDateTime.now().plusMinutes(30);
        this.expired = false;
        this.token = token;
    }

}