package com.infotech.docyard.dochandling.dto;

import lombok.Data;

@Data
public class UploadDocumentDTO {

    private Long ownerId;
    private Long folderId;
    private Long createdBy;
    private Long updatedBy;

    public UploadDocumentDTO() {
    }

}
