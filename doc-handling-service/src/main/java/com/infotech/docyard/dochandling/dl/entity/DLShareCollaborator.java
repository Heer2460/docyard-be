package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infotech.docyard.dochandling.enums.AccessRightEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DL_SHARE_COLLABORATORS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DLShareCollaborator extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DL_SHARE_ID", nullable = false)
    private DLShare dlShare;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COLLABORATOR_ID", nullable = false)
    private DLCollaborator dlCollaborator;

    @Column(name = "ACCESS_RIGHT", nullable = false)
    private String accessRight = AccessRightEnum.getDefault().getValue();

    public DLShareCollaborator() {
    }

    public DLShareCollaborator(DLShare dlShare, DLCollaborator dlCollaborator, String accessRight) {
        this.dlShare = dlShare;
        this.dlCollaborator = dlCollaborator;
        this.accessRight = accessRight;
    }

}
