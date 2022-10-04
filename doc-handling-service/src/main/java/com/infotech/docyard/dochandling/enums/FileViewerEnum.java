package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;

public enum FileViewerEnum {
    NOT_APPLICABLE("UNKNOWN"),
    IMAGE_VIEWER("FLASH"),
    PDF_VIEWER("FLASH");

    private String type;

    // Constructor
    private FileViewerEnum(String type) {
        this.type = type;
    }

    // static Map
    private static HashMap<String, FileViewerEnum> map = new HashMap<String, FileViewerEnum>();

    static {
        for (FileViewerEnum e : values())
            map.put(e.getType(), e);
    }

    // public methods

    public static FileViewerEnum getByType(String type) {
        return map.containsKey(type) ? map.get(type) : null;
    }

    public static boolean contains(String type) {
        return map.containsKey(type);
    }

    // public getters
    public String getType() {
        return type;
    }

}
