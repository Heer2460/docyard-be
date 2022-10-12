package com.infotech.docyard.dochandling.enums;

public enum ShareStatusEnum {

    INVITED("INVITED"),
    SHARED("SHARED");

    private String value;

    ShareStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
