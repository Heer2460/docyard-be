package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;

public enum FileEditorEnum {
    NOT_APPLICABLE("UNKNOWN"),
    FOLDER_EDITOR("FOLDER"),

    HTML_EDITOR("CK_EDITOR"),
    OFFICE_DOCUMENT_EDITOR("APPLET"),
    TEXT_EDITOR("I_FRAME"),
    IMAGE_EDITOR("FLASH"),
    PDF_EDITOR("FLASH"),

    FLV_EDITOR(NOT_APPLICABLE.getType());

    private String type;

    // Constructor
    private FileEditorEnum(String type) {
        this.type = type;
    }

    // static Map
    private static HashMap<String, FileEditorEnum> map = new HashMap<String, FileEditorEnum>();

    static {
        for (FileEditorEnum e : values())
            map.put(e.getType(), e);
    }

    // public methods

    public static FileEditorEnum getByType(String type) {
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
