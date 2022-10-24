package com.infotech.docyard.dochandling.dto;

import lombok.Data;

import java.util.List;

@Data
public class NameEmailDTO {
    List<String> emails;
    List<String> names;

    public NameEmailDTO() {

    }
    public NameEmailDTO(List<String> names, List<String> emails) {
        this.names = names;
        this.emails = emails;
    }
}
