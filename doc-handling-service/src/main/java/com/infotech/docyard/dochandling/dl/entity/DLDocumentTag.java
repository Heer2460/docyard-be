package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "DHS_DOCUMENT_TAG")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DLDocumentTag extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "MESSAGE")
    private String message;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_ID", nullable = false)
    private DLDocument dlDocument;

    public DLDocumentTag() {
    }

    public DLDocumentTag(Long userId, String message, DLDocument dlDocument) {
        this.userId = userId;
        this.message = message;
        this.dlDocument = dlDocument;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
    }
}
