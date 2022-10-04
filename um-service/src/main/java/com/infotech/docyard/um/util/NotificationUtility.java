package com.infotech.docyard.um.util;

import com.infotech.docyard.um.dl.entity.User;
import com.infotech.docyard.um.dto.UserDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NotificationUtility {

    public static String buildForgotPasswordEmailContent(User user, String baseFELink, String token) {
        log.info("buildForgotPasswordEmailContent API initiated...");

        String completeLink = baseFELink + "?token=" + token + "&userId=" + user.getId();
        StringBuilder content = new StringBuilder("Dear " + user.getName() + ", </br>");
        content.append("click on below link to reset your password ");
        content.append("</br> ");
        content.append(completeLink);
        content.append("</br> ");
        content.append("</br> ");

        content.append("Thanks ");

        return content.toString();
    }

    public static String buildCreateUserEmailContent(UserDTO userDTO, String baseFELink) {
        log.info("buildCreateUserEmailContent API initiated...");

        StringBuilder content = new StringBuilder("Welcome <strong>" + userDTO.getName() + " ! </strong>");
        content.append("<br>Your account has been created. To access the system use the following: <br>");
        content.append("URL: ").append(baseFELink).append("<br>");
        content.append("<strong> username: ").append(userDTO.getUsername()).append("</strong><br><br>");
        content.append("For password please contact your system administrator.");

        return content.toString();
    }
}
