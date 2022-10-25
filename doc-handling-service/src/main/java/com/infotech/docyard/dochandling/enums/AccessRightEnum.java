package com.infotech.docyard.dochandling.enums;

public enum AccessRightEnum {

    OWNER("OWNER"),
    VIEW("VIEW"),
    COMMENT("COMMENT"),
    EDITOR("EDITOR");
    private String value;

    AccessRightEnum(String value) {
        this.value = value;
    }

    public static AccessRightEnum getDefault() {
        return VIEW;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
