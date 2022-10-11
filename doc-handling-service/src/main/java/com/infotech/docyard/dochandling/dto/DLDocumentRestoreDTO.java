package com.infotech.docyard.dochandling.dto;

import lombok.Data;

import java.util.List;

@Data
public class DLDocumentRestoreDTO {
    private List<Long> dlDocumentIds;
}
