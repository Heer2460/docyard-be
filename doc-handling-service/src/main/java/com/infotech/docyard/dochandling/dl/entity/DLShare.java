package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infotech.docyard.dochandling.enums.AccessRightEnum;
import com.infotech.docyard.dochandling.enums.ShareTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "DL_SHARES")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DLShare extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DL_DOCUMENT_ID")
    private DLDocument dlDocument;

    @Column(name = "PERMANENT_LINK", unique = true)
    private String permanentLink;

    @Column(name = "SHARE_TYPE", nullable = false)
    private String shareType = ShareTypeEnum.getDefault().getValue();

    @Column(name = "ACCESS_RIGHT", nullable = false)
    private String accessRight = AccessRightEnum.getDefault().getValue();

    @Column(name = "SHARE_NOTES")
    private String shareNotes;

    @Column(name = "STATUS")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dlShare")
    private List<DLShareCollaborator> dlShareCollaborators;

    public DLShare() {
    }
}
