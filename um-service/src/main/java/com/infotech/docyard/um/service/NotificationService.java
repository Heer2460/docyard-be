package com.infotech.docyard.um.service;


import com.infotech.docyard.um.dl.entity.ConfigSMTP;
import com.infotech.docyard.um.dl.entity.EmailInstance;
import com.infotech.docyard.um.util.AppConstants;
import com.infotech.docyard.um.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Log4j2
public class NotificationService {

    @Autowired
    private EmailService emailService;
    private String fromEmail = null, fromPassword = null;

    public boolean sendEmail(EmailInstance emailInstance) {
        boolean isSent = false;
        Properties properties = null;

        ConfigSMTP configSMTP = emailService.getConfigSMTP();
        if (!AppUtility.isEmpty(configSMTP)) {
            properties = new Properties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.host", configSMTP.getSmtpServer()); // smtp.gmail.com?
            properties.put("mail.smtp.port", configSMTP.getSmtpPort());
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.smtp.starttls.enable", true);
            fromEmail = configSMTP.getSmtpUsername();
            fromPassword = configSMTP.getSmtpPassword();
        }
        if (!AppUtility.isEmpty(properties)) {
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, fromPassword);
                }
            });
            log.info("email instance Id>>. " + emailInstance.getId());
            isSent = this.routeEmail(emailInstance, session);
            if (isSent) {
                emailInstance.setStatus(AppConstants.EmailConstants.EMAIL_STATUS_SEND);
                emailService.saveUpdateEmailInstance(emailInstance);
            }
        }
        return isSent;
    }

    private boolean routeEmail(EmailInstance emailInstance, Session session) {
        boolean isSent = false;
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(fromEmail));
            if (!AppUtility.isEmpty(emailInstance.getToEmail())) {
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(emailInstance.getToEmail()));
            }
           /* if (!AppUtility.isEmpty(emailInstance.getToEmail())) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailInstance.getCc()));
            }
            if (!AppUtility.isEmpty(emailInstance.getBcc())) {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailInstance.getBcc()));
            }*/
            if (!AppUtility.isEmpty(emailInstance.getSubject())) {
                message.setSubject(emailInstance.getSubject());
            }
            if (!AppUtility.isEmpty(emailInstance.getContent())) {
                message.setContent(emailInstance.getContent(), "text/html; charset=utf-8");
            }
            Transport.send(message);
            isSent = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return isSent;
    }
}
