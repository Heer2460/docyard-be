package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table(name = "UM_DL_DOCUMENT_VERSIONS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DLDocumentVersion extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "IS_VISIBLE")
    private Boolean visible;

    @Column(name = "KEY_STRING")
    private String keyString;

    @Column(name = "GUID")
    @NotNull
    private String guId;

    @Column(name = "VERSION")
    private Double version;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_ID", nullable = false)
    private DLDocument dlDocument;

    public DLDocumentVersion() {
    }

}
