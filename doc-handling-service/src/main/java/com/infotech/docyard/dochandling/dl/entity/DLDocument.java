package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "DL_DOCUMENTS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DLDocument extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "IS_FOLDER")
    private Boolean isFolder;

    @Column(name = "IS_ARCHIVED", columnDefinition = "boolean default false")
    @NotNull
    private Boolean isArchived;

    @Column(name = "ARCCHIVED_ON")
    private ZonedDateTime archivedOn;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_FAVOURITE", columnDefinition = "boolean default false")
    private Boolean isFavourite;

    @Column(name = "GUID")
    @NotNull
    private String guId;

    @Column(name = "VERSION")
    private Double version;

    @Column(name = "VERSION_GUID")
    private String versionGUId;

    @Column(name = "IS_LEAF_NODE")
    @NotNull
    private Boolean isLeafNode;

    @Column(name = "NAME")
    @NotNull
    private String name;

    @Column(name = "SHARE_TYPE")
    private String shareType;

    @Column(name = "IS_SHARED")
    private Boolean isShared;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CURRENT_VERSION")
    private Double currentVersion;

    @Column(name = "EXTENSION")
    private String extension;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "SIZE")
    private String size;

    @Column(name = "TITLE")
    @NotNull
    private String title;

    @OneToMany(mappedBy = "dlDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DLDocumentActivity> documentActivities;

    @OneToMany(mappedBy = "dlDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DLDocumentComment> documentComments;

    @OneToMany(mappedBy = "dlDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DLDocumentVersion> documentVersions;

    public DLDocument() {
    }

    public DLDocument(Long id) {
        this.setId(id);
    }
}
