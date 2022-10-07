package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "DL_DOCUMENTS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DLDocument extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "IS_FOLDER")
    private Boolean folder;

    @Column(name = "IS_ARCHIVED", columnDefinition = "boolean default false")
    @NotNull
    private Boolean archived;

    @Column(name = "ARCCHIVED_ON")
    private ZonedDateTime archivedOn;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_FAVOURITE", columnDefinition = "boolean default false")
    private Boolean favourite;

    @Column(name = "VERSION")
    private Double version;

    @Column(name = "VERSION_GUID")
    private String versionGUId;

    @Column(name = "GUID")
    private String guId;

    @Column(name = "IS_LEAF_NODE")
    @NotNull
    private Boolean leafNode;

    @Column(name = "NAME")
    @NotNull
    private String name;

    @Column(name = "SHARE_TYPE")
    private String shareType;

    @Column(name = "IS_SHARED")
    private Boolean shared;

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

    @Column(name = "SIZE_BYTES")
    private Long sizeBytes;

    @Column(name = "TITLE")
    @NotNull
    private String title;


    @OneToMany(mappedBy = "dlDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DLDocumentComment> documentComments;

    @OneToMany(mappedBy = "dlDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DLDocumentVersion> documentVersions;

    public DLDocument() {
    }

    public DLDocument(Long id) {
        this.setId(id);
    }

    @Override
    public String toString() {

        return "DLDocument[id=" + this.getId() + ", title=" + title + ", sizeBytes=" + sizeBytes + ", size=" + size +
                ", mimeType=" + mimeType + ", location=" + location + ", extension=" + extension + ", currentVersion=" +
                currentVersion + ", content=" + content + ", subject=" + subject + ", shared=" + shared + ", shareType=" +
                shareType + ", name=" + name + ", leafNode=" + leafNode + ", guId=" + guId + ", versionGUId=" + versionGUId +
                ", version=" + version + ", favourite=" + favourite + ", description=" + description + ", archivedOn=" +
                archivedOn + ", archived=" + archived + ", folder=" + folder + ", parentId=" + parentId + "]";
    }
}
