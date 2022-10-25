package com.infotech.docyard.cjs.jobs;


import com.infotech.docyard.cjs.dl.entity.ConfigSMTP;
import com.infotech.docyard.cjs.dl.entity.EmailInstance;
import com.infotech.docyard.cjs.service.EmailService;
import com.infotech.docyard.cjs.util.AppConstants;
import com.infotech.docyard.cjs.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Component
@Log4j2
public class EmailSenderJob {

    @Autowired
    private EmailService emailService;

    private String fromEmail = null, fromPassword = null;

    private boolean sendEmail(EmailInstance emailInstance, Session session) {
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

    @Scheduled(cron = "0 * * * * *") // Each Minute
    public void run() {
        Properties properties = null;
        ConfigSMTP configSMTP = emailService.getConfigSMTP();
        if (!AppUtility.isEmpty(configSMTP)) {

            properties = new Properties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.host", configSMTP.getSmtpServer()); // smtp.gmail.com?
            properties.put("mail.smtp.port", configSMTP.getSmtpPort());
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            fromEmail = configSMTP.getSmtpUsername();
            fromPassword = configSMTP.getSmtpPassword();
        }
        if (!AppUtility.isEmpty(properties)) {
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, fromPassword);
                }
            });
            List<EmailInstance> emailInstanceList = emailService
                    .getAllByStatus(AppConstants.EmailConstants.EMAIL_STATUS_NOT_SEND);
            if (!emailInstanceList.isEmpty()) {
                for (EmailInstance emailInstance : emailInstanceList) {
                    log.info("email instance Id>>. " + emailInstance.getId());
                    if (this.sendEmail(emailInstance, session)) {
                        emailInstance.setStatus(AppConstants.EmailConstants.EMAIL_STATUS_SEND);
                        emailService.saveUpdateEmailInstance(emailInstance);
                    }
                }
            }
        }
    }
}
