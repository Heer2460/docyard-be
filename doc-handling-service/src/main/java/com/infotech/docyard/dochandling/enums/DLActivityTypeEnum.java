package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;

public enum DLActivityTypeEnum {

    COMMENT("COMMENT"),
    FILE_VIEWED("FILE_VIEWED"),
    FILE_DELETED("FILE_DELETED"),
    UPLOADED("UPLOADED"),
    DOWNLOADED("DOWNLOADED"),
    CREATED("CREATED"),
    STARRED("STARRED"),
    RENAMED("RENAMED"),
    ARCHIVED("ARCHIVED"),
    RESTORED_ARCHIVED("RESTORED_ARCHIVED"),
    COMMENT_POSTED("COMMENT_POSTED"),
    COMMENT_DELETED("COMMENT_DELETED"),
    OPEN_LINK_SHARE("SHARED_WITH_OPEN_LINK"),
    RESTRICTED_SHARE("RESTRICTED_SHARE"),
    ANY_ONE("ANY_ONE"),
    RESTRICTED("RESTRICTED"),
    NO_SHARING("NO_SHARING");

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

    public static DLActivityTypeEnum getByName(String name) {
        return map.get(name);
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
