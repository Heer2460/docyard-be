package com.infotech.docyard.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {

    private Long userId;
    private String currentPassword;
    private String newPassword;
    private String token;
}
