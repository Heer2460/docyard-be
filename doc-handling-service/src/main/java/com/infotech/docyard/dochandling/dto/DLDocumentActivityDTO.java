package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class DLDocumentActivityDTO extends BaseDTO<DLDocumentActivityDTO, DLDocumentActivity> implements Serializable {
    private String activityType;
    private Long entityId;
    private Long userId;
    private Long docId;

    public DLDocumentActivityDTO() {
    }

    @Override
    public DLDocumentActivity convertToEntity() throws IOException {
        DLDocumentActivity dlDocumentActivity = new DLDocumentActivity();
        dlDocumentActivity.setId(this.id);
        dlDocumentActivity.setActivityType(this.activityType);
        dlDocumentActivity.setEntityId(this.entityId);
        dlDocumentActivity.setUserId(this.userId);
        dlDocumentActivity.setDocId(this.docId);
        dlDocumentActivity.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        dlDocumentActivity.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        dlDocumentActivity.setCreatedBy(this.getCreatedBy());
        dlDocumentActivity.setUpdatedBy(this.getUpdatedBy());
        return dlDocumentActivity;
    }

    @Override
    public void convertToDTO(DLDocumentActivity entity, boolean partialFill) {
        this.id = entity.getId();
        this.activityType = entity.getActivityType();
        this.entityId = entity.getEntityId();
        this.userId = entity.getUserId();
        this.docId = entity.getDocId();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public DLDocumentActivityDTO convertToNewDTO(DLDocumentActivity entity, boolean partialFill) {
        DLDocumentActivityDTO dlDocumentActivityDTO = new DLDocumentActivityDTO();
        dlDocumentActivityDTO.convertToDTO(entity, partialFill);
        return dlDocumentActivityDTO;
    }

}
