package com.infotech.docyard.dochandling.dto;

import lombok.Data;

@Data
public class UserSpaceOccupiedDTO {

    private Long totalAllottedSize = 52428800000L;
    private Long totalUsedSpace;
    private String spaceUsedFormatted;

    public UserSpaceOccupiedDTO() {

    }

    public UserSpaceOccupiedDTO(Long totalUsedSpace, String spaceUsedFormatted) {
        this.totalUsedSpace = totalUsedSpace;
        this.spaceUsedFormatted = spaceUsedFormatted;
    }
}
