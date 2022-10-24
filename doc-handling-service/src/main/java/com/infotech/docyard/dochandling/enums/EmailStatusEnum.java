package com.infotech.docyard.dochandling.enums;

public enum EmailStatusEnum {

    SENT("SENT"),
    NOT_SENT("NOT_SENT");

    private String value;

    EmailStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
