package com.infotech.user.service.dto;

import com.infotech.user.service.entity.Tenant;
import com.infotech.user.service.entity.User;
import lombok.Data;

@Data
public class UserDTO {


    private User user;
    private Tenant tenant;
}
