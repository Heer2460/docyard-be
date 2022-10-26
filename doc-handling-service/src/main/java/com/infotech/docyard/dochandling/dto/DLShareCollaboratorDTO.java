package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLShareCollaborator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class DLShareCollaboratorDTO extends BaseDTO<DLShareCollaboratorDTO, DLShareCollaborator> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long dlShareId;
    private Long dlCollaboratorId;
    private String dlCollaboratorEmail;
    private String accessRight;

    public DLShareCollaboratorDTO() {
    }

    @Override
    public DLShareCollaborator convertToEntity() throws IOException {
        return null;
    }

    @Override
    public void convertToDTO(DLShareCollaborator entity, boolean partialFill) {
        this.dlShareId = entity.getDlShareId();
        this.dlCollaboratorId = entity.getDlCollaboratorId();
        this.accessRight = entity.getAccessRight();
    }

    @Override
    public DLShareCollaboratorDTO convertToNewDTO(DLShareCollaborator entity, boolean partialFill) {
        DLShareCollaboratorDTO dscDTO = new DLShareCollaboratorDTO();
        dscDTO.convertToDTO(entity, partialFill);

        return dscDTO;
    }
}
