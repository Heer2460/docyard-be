package com.infotech.docyard.cjs.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "DHS_DOCUMENT_ACTIVITIES")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DLDocumentActivity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "DOC_ID")
    private Long docId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ENTITY_ID")
    private Long entityId;

    @Column(name = "ACTIVITY_TYPE")
    private String activityType;

    public DLDocumentActivity() {
    }

    public DLDocumentActivity(Long actorId, String activityType, Long entityId, Long docId) {
        this.userId = actorId;
        this.activityType = activityType;
        this.entityId = entityId;
        this.docId = docId;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
    }
}
