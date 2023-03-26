package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentTag;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DateTimeUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class DLDocumentTagDTO extends BaseDTO<DLDocumentTagDTO, DLDocumentTag> implements Serializable {
    private String message;
    private Long userId;
    private String nameOfUser;
    private String postedOn;
    private Long docId;
    @Override
    public DLDocumentTag convertToEntity() {
        DLDocumentTag dlDocumentTag = new DLDocumentTag();
        dlDocumentTag.setId(this.id);
        dlDocumentTag.setMessage(this.message);
     //   dlDocumentTag.setMessage(Arrays.asList(message.split(" ")).toString());
        dlDocumentTag.setUserId(this.userId);
        dlDocumentTag.setDlDocument(new DLDocument(this.docId));
        dlDocumentTag.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        dlDocumentTag.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        dlDocumentTag.setCreatedBy(this.getCreatedBy());
        dlDocumentTag.setUpdatedBy(this.getUpdatedBy());
        return dlDocumentTag;
    }

    @Override
    public void convertToDTO(DLDocumentTag entity, boolean partialFill) {
        this.id = entity.getId();
        this.message = entity.getMessage();
        this.userId = entity.getUserId();
        this.postedOn = DateTimeUtil.convertDateToUFDateFormat(entity.getUpdatedOn());
        this.docId = entity.getDlDocument().getId();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public DLDocumentTagDTO convertToNewDTO(DLDocumentTag entity, boolean partialFill) {
        DLDocumentTagDTO dlDocumentTagDTO = new DLDocumentTagDTO();
        dlDocumentTagDTO.convertToDTO(entity, partialFill);
        return dlDocumentTagDTO;
    }
}
