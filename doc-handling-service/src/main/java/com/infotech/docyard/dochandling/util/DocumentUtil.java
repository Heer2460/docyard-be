package com.infotech.docyard.dochandling.util;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.StringTokenizer;

public class DocumentUtil {

    public static String getMimeType(String extension) {
        if (AppConstants.FileType.EXT_BMP.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_BMP;
        } else if (AppConstants.FileType.EXT_DOC.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_DOC;
        } else if (AppConstants.FileType.EXT_DOCX.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_DOCX;
        } else if (AppConstants.FileType.EXT_FLV.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_FLV;
        } else if (AppConstants.FileType.EXT_GIF.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_GIF;
        } else if (AppConstants.FileType.EXT_JPEG.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_JPEG;
        } else if (AppConstants.FileType.EXT_JPG.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_JPG;
        } else if (AppConstants.FileType.EXT_PNG.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_PNG;
        } else if (AppConstants.FileType.EXT_PPT.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_PPT;
        } else if (AppConstants.FileType.EXT_PPTX.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_PPTX;
        } else if (AppConstants.FileType.EXT_XLS.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_XLS;
        } else if (AppConstants.FileType.EXT_XLSX.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_XLSX;
        } else if (AppConstants.FileType.EXT_HTML.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_HTML;
        } else if (AppConstants.FileType.EXT_TXT.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_TXT;
        } else if (AppConstants.FileType.EXT_XHTML.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_XHTML;
        } else if (AppConstants.FileType.EXT_PDF.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_PDF;
        } else if (AppConstants.FileType.EXT_SQL.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_SQL;
        } else if (AppConstants.FileType.EXT_RAR.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_RAR;
        } else if (AppConstants.FileType.EXT_ZIP.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_ZIP;
        } else if (AppConstants.FileType.EXT_PSD.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_PSD;
        } else if (AppConstants.FileType.EXT_SVG.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_SVG;
        } else if (AppConstants.FileType.EXT_7ZIP.equalsIgnoreCase(extension)) {
            return AppConstants.MimeType.MIME_7ZIP;
        } else {
            return AppConstants.MimeType.MIME_UNKNOWN;
        }
    }

    public static String getFileSize(long bytes) {
        double fileSizeKB = 0;
        String fileLength = String.valueOf(bytes);
        int fileLengthDigitCount = fileLength.length();
        double fileLengthLong = bytes;
        String howBig = "";

        if (bytes > 0) {
            if (fileLengthDigitCount <= 6) {
                fileSizeKB = Math.abs((fileLengthLong / 1024));
                howBig = "KB";
            } else if (fileLengthDigitCount <= 9) {
                fileSizeKB = Math.abs(fileLengthLong / (1024 * 1024));
                howBig = "MB";
            } else {
                fileSizeKB = Math.abs((fileLengthLong / (1024 * 1024 * 1024)));
                howBig = "GB";
            }
        } else {
            howBig = "KB";
        }
        String finalResult = getRoundedValue(fileSizeKB);
        return finalResult + " " + howBig;
    }

    public static String getFileSize(Double bytes) {
        double fileSizeKB = 0;
        String fileLength = String.valueOf(bytes);
        int fileLengthDigitCount = fileLength.length();
        double fileLengthLong = bytes;
        String howBig = "";

        if (bytes > 0) {
            if (fileLengthDigitCount <= 6) {
                fileSizeKB = Math.abs((fileLengthLong / 1024));
                howBig = "KB";
            } else if (fileLengthDigitCount <= 9) {
                fileSizeKB = Math.abs(fileLengthLong / (1024 * 1024));
                howBig = "MB";
            } else {
                fileSizeKB = Math.abs((fileLengthLong / (1024 * 1024 * 1024)));
                howBig = "GB";
            }
        } else {
            howBig = "KB";
        }
        String finalResult = getRoundedValue(fileSizeKB);
        return finalResult + " " + howBig;
    }

    private static String getRoundedValue(double decimalVal) {
        long beforeDecimalValue = decimalTokenize(decimalVal, 1);
        long afterDecimalValue = decimalTokenize(decimalVal, 2);
        long decimalValueLength = String.valueOf(afterDecimalValue).length();
        long dividerVal = divider(decimalValueLength - 1);
        long dividedValue = afterDecimalValue / dividerVal;
        String finalResult = String.valueOf(beforeDecimalValue) + "."
                + String.valueOf(dividedValue);

        return finalResult;
    }

    private static long decimalTokenize(double decimalVal, int position) {

        long returnDecimalVal = 0;
        String strDecimalVal = "";

        if (decimalVal > 0)
            strDecimalVal = String.valueOf(decimalVal);

        if (strDecimalVal.length() > 0) {
            StringTokenizer decimalToken = new StringTokenizer(strDecimalVal,
                    ".");
            if (position == 1) {
                returnDecimalVal = Long.parseLong(decimalToken.nextToken());
            } else if (position == 2) {
                decimalToken.nextToken();
                returnDecimalVal = Long.parseLong(decimalToken.nextToken());
            }
        }
        return returnDecimalVal;
    }

    private static long divider(long argLength) {
        long varDivider = 1;

        for (int i = 0; i < (argLength - 1); i++) {
            varDivider = varDivider * 10;
        }
        return varDivider;
    }

    public static String buildPathSeparator(String customPathSeparator) {
        return (customPathSeparator != null) ? customPathSeparator : AppConstants.DL_PATH_SEPARATOR;
    }

    public static boolean isRootFolder(DLDocument dlDocument) {
        if (AppUtility.isEmpty(dlDocument)) {
            return true;
        }
        return false;
    }

    public static boolean isOCRType(DLDocument doc) {
        return doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_PNG) ||
                doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_JPG) ||
                doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_BMP) ||
                doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_JPEG);
    }
}
