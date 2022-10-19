package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLShareCollaborator;
import lombok.Data;

import java.io.Serializable;

@Data
public class DLDocumentShareDTO implements Serializable {

    private Long dlShareId;
    private String dlCollName;
    private String dlCollEmail;
    private String dlCollPic;
    private String accessRight;

    public DLDocumentShareDTO() {
    }

    public DLDocumentShareDTO(DLShareCollaborator dsc) {
        this.dlShareId = dsc.getDlShareId();
        this.accessRight = dsc.getAccessRight();
    }
}
