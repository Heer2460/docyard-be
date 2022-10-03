package com.infotech.docyard.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {

    private Long userId;
    private String token;
    private String newPassword;

}
