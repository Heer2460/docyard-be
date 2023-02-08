package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infotech.docyard.dochandling.enums.AccessRightEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "UM_DL_SHARE_COLLABORATORS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DLShareCollaborator extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "DL_SHARE_ID", nullable = false)
    private Long dlShareId;

    @Column(name = "DL_COLLABORATOR_ID", nullable = false)
    private Long dlCollaboratorId;

    @Column(name = "ACCESS_RIGHT", nullable = false)
    private String accessRight = AccessRightEnum.getDefault().getValue();

    public DLShareCollaborator() {
    }

}
