package com.infotech.tenant.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "TENANTS")
@JsonIgnoreProperties(ignoreUnknown = true)

@AllArgsConstructor
@NoArgsConstructor

public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CREATED_ON", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @CreatedDate
    private ZonedDateTime createdOn;

    @Column(name = "UPDATED_ON", columnDefinition = "TIMESTAMP")
    @LastModifiedDate
    private ZonedDateTime updatedOn;

    @Column(name = "CREATED_BY")
    @CreatedBy
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    @LastModifiedBy
    private Long updatedBy;

    @Column(name = "TENANT_NAME")
    private String tenantName;

    @Column(name = "BUSINESS_NAME")
    private String businessName;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "STATUS")
    private String status;


}
