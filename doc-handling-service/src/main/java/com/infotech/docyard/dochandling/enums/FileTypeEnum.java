package com.infotech.docyard.dochandling.enums;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public enum FileTypeEnum {
    FOLDER("folder"
            , ""
            , "folder"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.FOLDER_EDITOR
            , IconFileEnum.FOLDER
    ),

    UNKNOWN("unknown"
            , "unknown/unknown"
            , ""
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.UNKNOWN
    ),

    PPT("ppt"
            , "application/vnd.ms-powerpoint"
            , "Microsoft Office Power Point Presentation"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.OFFICE_DOCUMENT_EDITOR
            , IconFileEnum.PPT
    ),

    PPTX("pptx"
            , "application/vnd.ms-powerpoint"
            , "Microsoft Office Power Point Presentation"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.PPTX
    ),

    DOC("doc"
            , "application/msword"
            , "Microsoft Office Word DocumentResponseWrapper"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.OFFICE_DOCUMENT_EDITOR
            , IconFileEnum.DOC
    ),

    DOCX("docx"
            , "application/msword"
            , "Microsoft Office Word DocumentResponseWrapper"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.OFFICE_DOCUMENT_EDITOR
            , IconFileEnum.DOCX
    ),

    XLS("xls"
            , "application/vnd.ms-excel"
            , "Microsoft Office Excel Worksheet"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.OFFICE_DOCUMENT_EDITOR
            , IconFileEnum.XLS
    ),

    XLSX("xlsx"
            , "application/vnd.ms-excel"
            , "Microsoft Office Excel Worksheet"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.OFFICE_DOCUMENT_EDITOR
            , IconFileEnum.XLSX
    ),

    PNG("png"
            , "image/png"
            , "PNG"
            , FileViewerEnum.IMAGE_VIEWER
            , FileEditorEnum.IMAGE_EDITOR
            , IconFileEnum.IMAGE
    ),

    GIF("gif"
            , "image/gif"
            , "GIF"
            , FileViewerEnum.IMAGE_VIEWER
            , FileEditorEnum.IMAGE_EDITOR
            , IconFileEnum.IMAGE
    ),

    JPG("jpg"
            , "image/jpeg"
            , "JPG"
            , FileViewerEnum.IMAGE_VIEWER
            , FileEditorEnum.IMAGE_EDITOR
            , IconFileEnum.IMAGE
    ),

    BMP("bmp"
            , "image/bmp"
            , "BMP"
            , FileViewerEnum.IMAGE_VIEWER
            , FileEditorEnum.IMAGE_EDITOR
            , IconFileEnum.IMAGE
    ),

    JPEG("jpeg"
            , "image/jpeg"
            , "JPEG"
            , FileViewerEnum.IMAGE_VIEWER
            , FileEditorEnum.IMAGE_EDITOR
            , IconFileEnum.IMAGE
    ),

    NEF("nef"
            , "image/nef"
            , "nikon raw file"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.IMAGE
    ),

    TXT("txt"
            , "text/plain"
            , "TXT"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.TEXT
    ),

    HTML("html"
            , "text/html"
            , "HTML"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.HTML_EDITOR
            , IconFileEnum.HTML
    ),

    HTM("htm"
            , "text/html"
            , "HTML"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.HTML_EDITOR
            , IconFileEnum.HTML
    ),
    XHTML("xhtml"
            , "text/html"
            , "HTML"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.HTML_EDITOR
            , IconFileEnum.HTML
    ),

    PDF("pdf"
            , "application/pdf"
            , "Adobe Acrobat DocumentResponseWrapper"
            , FileViewerEnum.PDF_VIEWER
            , FileEditorEnum.PDF_EDITOR
            , IconFileEnum.PDF
    ),

    FLV("flv"
            , "video/x-flv"
            , "FLV"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.FLV_EDITOR
            , IconFileEnum.VIDEO
    ),

    EXE("exe"
            , "application/exe"
            , "EXE"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.EXE
    ),

    MP3("mp3"
            , "audio/mpeg3"
            , "MP3"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.AUDIO
    ),

    CSV("csv"
            , "text/comma-separated-values"
            , "CSV"
            , FileViewerEnum.NOT_APPLICABLE
            , FileEditorEnum.NOT_APPLICABLE
            , IconFileEnum.CSV
    ),


    ;
    private String extension;
    private String mime;
    private String fileType;
    private FileEditorEnum supportedEditor;
    private FileViewerEnum supportedViewer;
    private IconFileEnum iconFile;


    // Constructor
    private FileTypeEnum(String extension, String mime, String fileType,
                         FileViewerEnum supportedViewer, FileEditorEnum supportedEditor, IconFileEnum iconFile) {
        this.extension = extension;
        this.mime = mime;
        this.fileType = fileType;
        this.supportedViewer = supportedViewer;
        this.supportedEditor = supportedEditor;
        this.iconFile = iconFile;
    }

    // static Map
    private static HashMap<String, FileTypeEnum> extMap = new HashMap<>();

    static {
        for (FileTypeEnum e : values())
            extMap.put(e.getExtension(), e);

        // validate iconFiles
        for (FileTypeEnum e : values()) {
            for (IconSizeEnum s : IconSizeEnum.values()) {
                String path = getFileIcon(e.getExtension(), s);
                File file = new File(path);
                if (!file.exists()) {
                    Logger.getLogger("FileTypeEnum").severe("Icon file Not Found: " + path);
                }
            }
        }


    }

    /* **********************************************************************************************
     * public methods
     * **********************************************************************************************/

    /**
     * @param extention
     * @return {@link FileTypeEnum}
     * @Description Returns FileTypeEnum Object for the given file extension
     * returns null if extension not found
     */
    public static FileTypeEnum getByExtention(String extention) {
        extention = extention.toLowerCase();
        return extMap.containsKey(extention) ? extMap.get(extention) : null;
    }

    /**
     * @param extension
     * @return {@link FileTypeEnum}
     * @Description Returns FileTypeEnum Object for the given file extension
     * returns unknown if extension not found
     */
    public static FileTypeEnum getByExtensionForcefully(String extension) {
        FileTypeEnum fte = getByExtention(extension);
        return fte != null ? fte : UNKNOWN;
    }

    /**
     * @param extension
     * @return boolean
     * @Description Returns true if FileTypeEnum contains the given extension
     */
    public static boolean contains(String extension) {
        return extMap.containsKey(extension);
    }

    /**
     * @param extension
     * @param size
     * @return
     * @Description Returns icon path for specific file type in given size
     * a related file should be placed named like this
     * => image-small.png
     */
    public static String getFileIcon(final String extension, final IconSizeEnum size) {
        String iconName = FileTypeEnum.getByExtensionForcefully(extension).iconFile.getFileName();
        String iconSize = ((size == null) ? IconSizeEnum.DEFAULT : size).getIconSize();
        StringBuilder path = new StringBuilder()
                .append("/resources/file-icons/")
                .append(iconName + "-" + iconSize)
                .append(".png");
        return path.toString();
    }

    /**
     * @param extension
     * @return
     * @Description Returns file type for the given extension.<br/>
     * <ul><li>if FileTypeEnum contains the given extension then it returns the File Type,
     * for example extension doc will return "Microsoft Word DocumentResponseWrapper".</li>
     * <li>if FileTypeEnum dosn't contains the given extension, then it will return the extension as file type, for example extension "jar" will returns "JAR"
     */
    public static String getFileType(String extension) {
        String fileType = "";
        if (FileTypeEnum.contains(extension)) {
            fileType = FileTypeEnum.getByExtention(extension).getFileType();
        } else if (extension != null) {
            fileType = extension.toUpperCase();
        }
        return fileType;
    }

    /**
     * @param extension
     * @return {@link String}
     * @Description returns supported editor for supported fileType, <br/>for example CK_EDITOR for html
     */
    public static String getFileEditorType(String extension) {
        FileTypeEnum fileType = FileTypeEnum.getByExtention(extension);
        if (fileType == null) {
            fileType = FileTypeEnum.UNKNOWN;
        }
        return fileType.getSupportedEditor().getType();
    }

    public static String getFileViewerType(String extension) {
        FileTypeEnum fileType = FileTypeEnum.getByExtention(extension);
        if (fileType == null) {
            fileType = FileTypeEnum.UNKNOWN;
        }
        return fileType.getSupportedViewer().getType();
    }




    /* **********************************************************************************************
     * public Getters, (* No Setter Required, or make them private)
     * **********************************************************************************************/

    public String getExtension() {
        return extension;
    }

    public String getMime() {
        return mime;
    }

    public String getFileType() {
        return fileType;
    }

    public FileEditorEnum getSupportedEditor() {
        return supportedEditor;
    }

    public FileViewerEnum getSupportedViewer() {
        return supportedViewer;
    }

}
