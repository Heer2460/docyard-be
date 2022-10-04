package com.infotech.docyard.um.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {

    private Long userId;
    private String token;
    private String newPassword;

}
