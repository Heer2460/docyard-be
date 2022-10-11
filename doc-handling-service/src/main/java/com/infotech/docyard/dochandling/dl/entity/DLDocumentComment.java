package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "DL_DOCUMENT_COMMENTS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DLDocumentComment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "MESSAGE")
    private String message;

    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_ID", nullable = false)
    private DLDocument dlDocument;

    public DLDocumentComment() {
    }

    public DLDocumentComment(Long userId, String message, DLDocument dlDocument) {
        this.userId = userId;
        this.message = message;
        this.dlDocument = dlDocument;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
    }

}
