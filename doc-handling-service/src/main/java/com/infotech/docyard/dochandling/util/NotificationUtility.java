package com.infotech.docyard.dochandling.util;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.dto.UserDTO;
import com.infotech.docyard.dochandling.dto.NameEmailDTO;
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

    public static String buildRestrictedShareFileEmailContent(UserDTO ownerDTO, String toName, String filename, String baseFELink) {
        log.info("buildShareFileEmailContent API initiated...");

        StringBuilder content = new StringBuilder("Welcome <strong>" + toName + " ! </strong>");
        content.append("<br>" + ownerDTO.getName() + " has shared a folder / file " + filename + " with you.</br></br>");
        content.append("For any query please contact your system administrator.");

        return content.toString();
    }

    public static String buildOpenShareFileEmailContent(UserDTO ownerDTO, String toName, String filename, String baseFELink) {
        log.info("buildShareFileEmailContent API initiated...");

        StringBuilder content = new StringBuilder("Welcome <strong>" + toName + " ! </strong>");
        content.append("<br>" + ownerDTO.getName() + " has shared a folder / file " + filename + " with you.</br>");
        content.append("<br>Please use following URL to access the file / folder.</br>");
        content.append("<strong>URL : " + baseFELink + "</strong></br></br>");
        content.append("For any query please contact your system administrator.");

        return content.toString();
    }
}
