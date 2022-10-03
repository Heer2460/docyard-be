package com.infotech.docyard.dto;

import lombok.Data;

@Data
public class ForgetPasswordDTO {

    private UserDTO userDTO;
    private String passwordResetLink;
}
