package com.infotech.docyard.dochandling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFolderDTO {
    private Long folderId;
    private Long ownerId;
    private Long createdBy;
    private Long updatedBy;
}
