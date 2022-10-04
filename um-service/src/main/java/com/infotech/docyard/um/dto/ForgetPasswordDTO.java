package com.infotech.docyard.um.dto;

import lombok.Data;

@Data
public class ForgetPasswordDTO {

    private UserDTO userDTO;
    private String passwordResetLink;
}
