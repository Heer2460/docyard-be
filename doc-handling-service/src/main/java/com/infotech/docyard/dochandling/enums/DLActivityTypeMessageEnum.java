package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;
import java.util.Objects;

public enum DLActivityTypeMessageEnum {
    COMMENT("has commented"),
    FILE_VIEWED("has viewed"),
    UPLOADED("has uploaded"),
    DOWNLOADED("has downloaded"),
    CREATED("has created"),
    RENAMED("has renamed"),
    ARCHIVED("has archived");

    private static final HashMap<String, DLActivityTypeMessageEnum> map = new HashMap<>();

    static {
        for (DLActivityTypeMessageEnum e : values()) {
            map.put(e.getValue(), e);
        }
    }

    private final String value;

    DLActivityTypeMessageEnum(String value) {
        this.value = value;
    }

    public static DLActivityTypeMessageEnum getByValue(String value) {
        return map.get(value);
    }

    public final String getValue() {
        return value;
    }

    public final boolean hasEqualValue(String value) {
        return equals(getByValue(value));
    }

    public final boolean equals(DLActivityTypeEnum e) {
        return e != null && (Objects.equals(getValue(), e.getValue()));
    }
}
