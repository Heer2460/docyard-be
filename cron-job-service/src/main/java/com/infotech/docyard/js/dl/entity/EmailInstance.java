package com.infotech.docyard.js.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "EMAIL_INSTANCES")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailInstance extends BaseEntity {

    @Column(name = "TO_EMAIL")
    private String toEmail;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "STATUS")
    private String status;

    public EmailInstance() {
    }

}