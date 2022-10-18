package com.infotech.docyard.dochandling.enums;

public enum ShareTypeEnum {

    NO_SHARING("NO_SHARING"),
    ANYONE("ANYONE"),
    RESTRICTED("RESTRICTED");

    private String value;

    ShareTypeEnum(String value) {
        this.value = value;
    }

    public static ShareTypeEnum getDefault() {
        return NO_SHARING;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
