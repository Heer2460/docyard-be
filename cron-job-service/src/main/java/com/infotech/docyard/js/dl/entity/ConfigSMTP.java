package com.infotech.docyard.js.dl.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "CONFIG_SMTP")
public class ConfigSMTP extends BaseEntity {

    @Column(name = "SMTP_SERVER")
    private String smtpServer;

    @Column(name = "SMTP_PORT")
    private String smtpPort;

    @Column(name = "SMTP_USERNAME")
    private String smtpUsername;

    @Column(name = "SMTP_PASSWORD")
    private String smtpPassword;
}