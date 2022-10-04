package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentVersion;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class DLDocumentVersionDTO extends BaseDTO<DLDocumentVersionDTO, DLDocumentVersion> implements Serializable {
    private Long userId;
    private Long docId;
    private Double version;
    private String guId;
    private String keyString;
    private Boolean isVisible;

    public DLDocumentVersionDTO() {
    }

    @Override
    public DLDocumentVersion convertToEntity() throws IOException {
        DLDocumentVersion dlDocumentVersion = new DLDocumentVersion();
        dlDocumentVersion.setId(this.id);
        dlDocumentVersion.setVersion(this.version);
        dlDocumentVersion.setGuId(this.guId);
        dlDocumentVersion.setKeyString(this.keyString);
        dlDocumentVersion.setVisible(this.isVisible);
        dlDocumentVersion.setUserId(this.userId);
        dlDocumentVersion.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        dlDocumentVersion.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        dlDocumentVersion.setCreatedBy(this.getCreatedBy());
        dlDocumentVersion.setUpdatedBy(this.getUpdatedBy());
        return dlDocumentVersion;
    }

    @Override
    public void convertToDTO(DLDocumentVersion entity, boolean partialFill) {
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.guId = entity.getGuId();
        this.keyString = entity.getKeyString();
        this.isVisible = entity.getVisible();
        this.userId = entity.getUserId();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public DLDocumentVersionDTO convertToNewDTO(DLDocumentVersion entity, boolean partialFill) {
        DLDocumentVersionDTO dlDocumentActivityDTO = new DLDocumentVersionDTO();
        dlDocumentActivityDTO.convertToDTO(entity, partialFill);
        return dlDocumentActivityDTO;
    }

}
