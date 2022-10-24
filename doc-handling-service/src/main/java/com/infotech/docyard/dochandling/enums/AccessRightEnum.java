package com.infotech.docyard.dochandling.enums;

public enum AccessRightEnum {

    OWNER("OWNER"),
    VIEWER("VIEWER"),
    COMMENTOR("COMMENTOR"),
    EDITOR("EDITOR");
    private String value;

    AccessRightEnum(String value) {
        this.value = value;
    }

    public static AccessRightEnum getDefault() {
        return VIEWER;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
