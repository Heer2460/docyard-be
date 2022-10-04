package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DL_DOCUMENTS_COMMENTS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DLDocumentComment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "DOC_ID")
    private Long docId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "MESSAGE")
    private String message;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID", nullable = false)
    private DLDocument dlDocument;

    public DLDocumentComment() {
    }

}
