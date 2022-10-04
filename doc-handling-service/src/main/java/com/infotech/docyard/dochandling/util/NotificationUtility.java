package com.infotech.docyard.dochandling.util;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NotificationUtility {

    public static String buildForgotPasswordEmailContent(DLDocument dlDocument, String baseFELink, String token) {
        log.info("buildForgotPasswordEmailContent API initiated...");
        return "not done yet";
    }

    public static String buildCreateUserEmailContent(DLDocumentDTO userDTO, String baseFELink) {
        log.info("buildCreateUserEmailContent API initiated...");
        return "not done yet";
    }
}
