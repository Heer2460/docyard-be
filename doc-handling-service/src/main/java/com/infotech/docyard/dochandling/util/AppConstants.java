package com.infotech.docyard.dochandling.util;

public class AppConstants {

    public static final String DL_PATH_SEPARATOR = "/";
    public static final double FIRST_VERSION = 1.0;

    public static class DateTimeFormat {
        static final String DATE_PATTERN_SAME_YEAR = "MMM d";
        public static final String DATE_PATTERN_PREVIOUS_YEAR = "MMM d,yyyy";
        public static final String TIME_PATTERN = "h:mm a";
        public static final String DATE_FORMAT_ONE = "d-M-yyyy";
        public static final String DATE_FORMAT_TWO = "d-MMM-yyyy";
        public static final String DATE_FORMAT_THREE = "MMM-yyyy";
    }

    public static class MimeType {
        public static final String MIME_PPT = "application/vnd.ms-powerpoint";
        public static final String MIME_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        public static final String MIME_DOC = "application/msword";
        public static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String MIME_XLS = "application/vnd.ms-excel";
        public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String MIME_PNG = "image/png";
        public static final String MIME_GIF = "image/gif";
        public static final String MIME_JPG = "image/jpeg";
        public static final String MIME_BMP = "image/bmp";
        public static final String MIME_JPEG = "image/jpeg";
        public static final String MIME_FLV = "video/x-flv";
        public static final String MIME_UNKNOWN = "unknown";
        public static final String MIME_TXT = "text/plain";
        public static final String MIME_HTML = "text/html";
        public static final String MIME_XHTML = "application/xhtml+xml";
        public static final String MIME_PDF = "application/pdf";
        public static final String MIME_SQL = "application/sql";
        public static final String MIME_RAR = "application/x-rar-compressed";
        public static final String MIME_ZIP = "application/zip";
        public static final String MIME_7ZIP = "application/x-7z-compressed";
        public static final String MIME_PSD = "application/photoshop";
        public static final String MIME_SVG = "image/svg+xml";
    }

    public static class FileType {
        public static final String EXT_PPT = "ppt";
        public static final String EXT_PPTX = "pptx";
        public static final String EXT_XLS = "xls";
        public static final String EXT_XLSX = "xlsx";
        public static final String EXT_PNG = "png";
        public static final String EXT_GIF = "gif";
        public static final String EXT_JPG = "jpg";
        public static final String EXT_JPEG = "jpeg";
        public static final String EXT_FLV = "flv";
        public static final String EXT_TXT = "txt";
        public static final String EXT_HTML = "html";
        public static final String EXT_XHTML = "xhtml";
        public static final String EXT_PDF = "pdf";
        public static final String EXT_SQL = "sql";
        public static final String EXT_RAR = "rar";
        public static final String EXT_ZIP = "zip";
        public static final String EXT_SVG = "svg";
        public static final String EXT_PSD = "psd";
        public static final String EXT_7ZIP = "7z";
        public static final String EXT_BMP = "bmp";
        public static final String EXT_DOC = "doc";
        public static final String EXT_DOCX = "docx";
    }
    public static class EmailSubjectConstants {
        public final static String SHARE_FILE = "File Shared";
        public final static String PASSWORD_EXPIRED = "Password Expired";
        public final static String FORGOT_PASSWORD = "Forgot Password";
        public final static String CHANGE_PASSWORD = "Change Password";
        public final static String USER_CREATED = "User Created";
        public final static String GD_ASSIGNED_EXAMINER = "Goods Declaration Assigned";
    }

    public static class EmailConstants {
        public static final String EMAIL_STATUS_NOT_SEND = "NOT_SENT";
        public static final String EMAIL_STATUS_SEND = "SENT";

    }
}
