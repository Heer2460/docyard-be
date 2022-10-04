package com.infotech.docyard.um.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {

    private Long userId;
    private String currentPassword;
    private String newPassword;
    private String token;
}
