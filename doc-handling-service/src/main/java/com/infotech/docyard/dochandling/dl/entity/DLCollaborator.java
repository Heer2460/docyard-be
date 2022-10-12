package com.infotech.docyard.dochandling.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "DL_COLLABORATORS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DLCollaborator extends BaseEntity implements Serializable {

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dlCollaborator")
    private List<DLShareCollaborator> dlShareCollaborators;

    public DLCollaborator() {
    }

}
