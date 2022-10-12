package com.infotech.docyard.dochandling.dto;

import lombok.Data;

@Data
public class ShareRequestDTO {

    private long dlDocId;
    private boolean folder;
    private String shareType;
    private String shareLink;
    private String[] collaborators;
    private String sharePermission;
    private String message;
    private String appContextPath;
    private String externalUserShareLink;

    public ShareRequestDTO() {
    }
}
