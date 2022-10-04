package com.infotech.docyard.dochandling.enums;

public enum IconFileEnum {
    UNKNOWN("unknown"),
    FOLDER("folder"),

    PPT("ppt"),
    PPTX("pptx"),
    DOC("doc"),
    DOCX("docx"),
    XLS("xls"),
    XLSX("xlsx"),

    IMAGE("image"),
    TEXT("text"),
    HTML("html"),
    HTM("htm"),
    XHTML("xhtml"),
    PDF("pdf"),

    EXE("binary"),

    VIDEO("video"),
    ZIP("zip"),
    XML("xml"),
    AUDIO("audio"),

    CSV("csv");

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    private IconFileEnum(String fileName) {
        this.fileName = fileName;
    }
}
