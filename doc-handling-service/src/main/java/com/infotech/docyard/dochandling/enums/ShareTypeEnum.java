package com.infotech.docyard.dochandling.enums;

public enum ShareTypeEnum {

    NO_SHARING("NO_SHARING"),
    OFF_SPECIFIC("OFF_SPECIFIC"),
    INVITED_PEOPLE_ONLY("INVITED_PEOPLE_ONLY"),
    INVITED_EXTERNAL_PEOPLE_ONLY("INVITED_EXTERNAL_PEOPLE_ONLY");

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
