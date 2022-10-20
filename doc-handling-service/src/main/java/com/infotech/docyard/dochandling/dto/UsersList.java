package com.infotech.docyard.dochandling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsersList {
    private List<UserDTO> userDTOList;

    public UsersList () {
        
    }
}
