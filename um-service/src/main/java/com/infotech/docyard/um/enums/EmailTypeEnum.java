package com.infotech.docyard.um.enums;

public enum EmailTypeEnum {

    EXPIRY_NOTIFICATION("EXPIRY_NOTIFICATION"),
    FORGOT_PASSWORD("FORGOT_PASSWORD"),
    CHANGE_PASSWORD("CHANGE_PASSWORD"),
    USER_CREATED("USER_CREATED"),
    GD_ASSIGNED_TO_EXAMINER("GD_ASSIGNED_TO_EXAMINER");

    private String value;

    EmailTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
