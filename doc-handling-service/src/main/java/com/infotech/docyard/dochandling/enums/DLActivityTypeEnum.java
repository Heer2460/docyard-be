package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;

public enum DLActivityTypeEnum {

    COMMENT("COMMENT"),
    FILE_VIEWED("FILE_VIEWED"),
    UPLOADED("UPLOADED"),
    DOWNLOADED("DOWNLOADED"),
    CREATED("CREATED"),
    ARCHIVED("ARCHIVED");

    private static HashMap<String, DLActivityTypeEnum> map = new HashMap<>();

    static {
        for (DLActivityTypeEnum e : values()) {
            map.put(e.getValue(), e);
        }
    }

    private String value;

    private DLActivityTypeEnum(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }

    public static final DLActivityTypeEnum getByValue(String value) {
        return map.get(value);
    }

    public final boolean hasEqualValue(String value) {
        return equals(getByValue(value));
    }

    public final boolean equals(DLActivityTypeEnum e) {
        return e == null ? false : (getValue() == e.getValue());
    }

}
