package com.infotech.docyard.um.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomResponseDTO {

    private List<Object> data;
    private String message;

    public CustomResponseDTO() {

    }

}
