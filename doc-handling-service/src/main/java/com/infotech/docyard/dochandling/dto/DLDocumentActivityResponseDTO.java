package com.infotech.docyard.dochandling.dto;

import lombok.Data;

import java.io.IOException;
import java.io.Serializable;

@Data
public class DLDocumentActivityResponseDTO extends BaseDTO implements Serializable {

    private String userName;
    private String action;
    private String docName;
    private String activityPerformedOn;

    public DLDocumentActivityResponseDTO (String userName, String action, String docName, String activityPerformedOn) {
        this.userName = userName;
        this.action = action;
        this.docName = docName;
        this.activityPerformedOn = activityPerformedOn;
    }

    public DLDocumentActivityResponseDTO () {

    }


    @Override
    public Object convertToEntity() throws IOException {
        return null;
    }

    @Override
    public void convertToDTO(Object entity, boolean partialFill) {

    }

    @Override
    public Object convertToNewDTO(Object entity, boolean partialFill) {
        return null;
    }

    @Override
    public String toString() {
        return userName + " " + action + " " + docName + " " + activityPerformedOn + ".";
    }
}
