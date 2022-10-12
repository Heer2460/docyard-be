package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;

public enum DLActivityTypeEnum {

    COMMENT("COMMENT"),
    FILE_VIEWED("FILE_VIEWED"),
    UPLOADED("UPLOADED"),
    DOWNLOADED("DOWNLOADED"),
    CREATED("CREATED"),
    RENAMED("RENAMED"),
    ARCHIVED("ARCHIVED"),
    COMMENT_POSTED("COMMENT_POSTED"),
    COMMENT_DELETED("COMMENT_DELETED"),
    INVITED_PEOPLE_ONLY("INVITED_PEOPLE_ONLY"),
    INVITED_EXTERNAL_PEOPLE_ONLY("INVITED_EXTERNAL_PEOPLE_ONLY"),
    OPEN_LINK("OPEN_LINK"),
    SHARING_DISABLED("SHARING_DISABLED");

    private static final HashMap<String, DLActivityTypeEnum> map = new HashMap<>();

    static {
        for (DLActivityTypeEnum e : values()) {
            map.put(e.getValue(), e);
        }
    }

    private final String value;

    DLActivityTypeEnum(String value) {
        this.value = value;
    }

    public static DLActivityTypeEnum getByValue(String value) {
        return map.get(value);
    }

    public final String getValue() {
        return value;
    }

    public final boolean hasEqualValue(String value) {
        return equals(getByValue(value));
    }

    public final boolean equals(DLActivityTypeEnum e) {
        return e != null && (getValue() == e.getValue());
    }

}
