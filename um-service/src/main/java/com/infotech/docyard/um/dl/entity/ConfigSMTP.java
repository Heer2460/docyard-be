package com.infotech.docyard.um.dl.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "UM_CONFIG_SMTP")
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
