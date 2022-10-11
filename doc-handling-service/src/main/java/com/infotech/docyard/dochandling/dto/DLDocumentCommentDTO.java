package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DateTimeUtil;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class DLDocumentCommentDTO extends BaseDTO<DLDocumentCommentDTO, DLDocumentComment> implements Serializable {
    private String message;
    private Long userId;
    private String nameOfUser;
    private String postedOn;
    private Long docId;

    public DLDocumentCommentDTO() {
    }

    @Override
    public DLDocumentComment convertToEntity() {
        DLDocumentComment dlDocumentComment = new DLDocumentComment();
        dlDocumentComment.setId(this.id);
        dlDocumentComment.setMessage(this.message);
        dlDocumentComment.setUserId(this.userId);
        dlDocumentComment.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        dlDocumentComment.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        dlDocumentComment.setCreatedBy(this.getCreatedBy());
        dlDocumentComment.setUpdatedBy(this.getUpdatedBy());
        return dlDocumentComment;
    }

    @Override
    public void convertToDTO(DLDocumentComment entity, boolean partialFill) {
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
    public DLDocumentCommentDTO convertToNewDTO(DLDocumentComment entity, boolean partialFill) {
        DLDocumentCommentDTO dlDocumentCommentDTO = new DLDocumentCommentDTO();
        dlDocumentCommentDTO.convertToDTO(entity, partialFill);
        return dlDocumentCommentDTO;
    }

}
