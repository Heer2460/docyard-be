package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLShare;
import com.infotech.docyard.dochandling.dl.entity.DLShareCollaborator;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DLShareDTO extends BaseDTO<DLShareDTO, DLShare> implements Serializable {

    private Long dlDocumentId;
    private String permanentLink;
    private String shareType;
    private String accessRight;
    private String shareNotes;
    private String status;
    private List<DLShareCollaboratorDTO> dlShareCollaboratorDTOList;

    public DLShareDTO() {

    }

    @Override
    public DLShare convertToEntity() throws IOException {
        return null;
    }

    @Override
    public void convertToDTO(DLShare entity, boolean partialFill) {
        this.id = entity.getId();
        this.dlDocumentId = entity.getDlDocumentId();
        this.permanentLink = entity.getPermanentLink();
        this.shareType = entity.getShareType();
        this.accessRight = entity.getAccessRight();
        this.shareNotes = entity.getShareNotes();
        this.status = entity.getStatus();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
        if (!partialFill) {
            this.fillDlShareCollaborators(entity.getDlShareCollaborators());
        }
    }

    @Override
    public DLShareDTO convertToNewDTO(DLShare entity, boolean partialFill) {
        DLShareDTO dlShareDTO = new DLShareDTO();
        dlShareDTO.convertToDTO(entity, partialFill);

        return dlShareDTO;
    }

    private void fillDlShareCollaborators(List<DLShareCollaborator> dlShareCollaboratorList) {
        if (AppUtility.isEmpty(this.dlShareCollaboratorDTOList)) {
            this.dlShareCollaboratorDTOList = new ArrayList<>();
        } else {
            this.dlShareCollaboratorDTOList.clear();
        }
        for (DLShareCollaborator sc : dlShareCollaboratorList) {
            this.dlShareCollaboratorDTOList.add(new DLShareCollaboratorDTO().convertToNewDTO(sc, false));
        }
    }
}
