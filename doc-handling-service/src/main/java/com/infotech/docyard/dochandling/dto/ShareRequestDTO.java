package com.infotech.docyard.dochandling.dto;

import lombok.Data;

@Data
public class ShareRequestDTO {

    private long dlDocId;
    private boolean folder;
    private String shareType;
    private String shareLink;
    private String[] dlCollaborators;
    private String sharePermission;
    private String message;
    private String appContextPath;
    private String externalUserShareLink;
    private Long userId;
    private Long departmentId;

    public ShareRequestDTO() {
    }
}
