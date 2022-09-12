package com.infotech.user.service.entity;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Tenant {

    private Long id;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
    private Long createdBy;
    private Long updatedBy;
    private String tenantName;
    private String businessName;
    private String address;
}
