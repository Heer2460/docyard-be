package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.ConfigSMTP;
import com.infotech.docyard.dochandling.dl.entity.EmailInstance;
import com.infotech.docyard.dochandling.dl.repository.EmailInstanceRepository;
import com.infotech.docyard.dochandling.dto.ShareRequestDTO;
import com.infotech.docyard.dochandling.dto.UserDTO;
import com.infotech.docyard.dochandling.enums.EmailStatusEnum;
import com.infotech.docyard.dochandling.enums.EmailTypeEnum;
import com.infotech.docyard.dochandling.util.AppConstants;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.NotificationUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

@Service
@Log4j2
public class NotificationService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EmailInstanceRepository emailInstanceRepository;
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

    public String sendShareNotification(ShareRequestDTO shareRequest, String docName, List<String> names, List<String> emails) {
        try {
            UserDTO ownerDTO = new UserDTO();
            Boolean emailed = false;
            Object response = restTemplate.getForObject("http://um-service/um/user/" + shareRequest.getUserId(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                ownerDTO.setName((String) map.get("name"));
                ownerDTO.setEmail((String) map.get("email"));
                ownerDTO.setUsername((String) map.get("username"));
            }
            for (int i = 0; i <= names.size() - 1; i++) {
                String content = null;
                if (shareRequest.getShareType().equals("RESTRICTED")) {
                    content = NotificationUtility.buildRestrictedShareFileEmailContent(ownerDTO, names.get(i), docName, shareRequest.getAppContextPath() +
                            shareRequest.getShareLink());
                }
                else if (shareRequest.getShareType().equals("ANYONE")){
                    content = NotificationUtility.buildOpenShareFileEmailContent(ownerDTO, names.get(i), docName, shareRequest.getAppContextPath() +
                            shareRequest.getShareLink());
                }
                emailed = false;
                if (!AppUtility.isEmpty(content)) {
                    EmailInstance emailInstance = new EmailInstance();
                    emailInstance.setToEmail(emails.get(i));
                    if (shareRequest.getShareType().equals("RESTRICTED")) {
                        emailInstance.setType(EmailTypeEnum.SHARE_FILE_RESTRICTED.getValue());
                        emailInstance.setSubject(AppConstants.EmailSubjectConstants.SHARE_FILE);
                    }
                    else if (shareRequest.getShareType().equals("ANYONE")){
                        emailInstance.setType(EmailTypeEnum.SHARE_FILE_WITH_ANYONE.getValue());
                        emailInstance.setSubject(AppConstants.EmailSubjectConstants.SHARE_FILE);
                    }
                    emailInstance.setContent(content);
                    emailInstance.setStatus(EmailStatusEnum.NOT_SENT.getValue());
                    emailInstance.setCreatedOn(ZonedDateTime.now());
                    emailInstance.setUpdatedOn(ZonedDateTime.now());
                    emailInstance.setCreatedBy(1L);
                    emailInstance.setUpdatedBy(1L);
                    emailInstanceRepository.save(emailInstance);
                    emailed = true;
                }
            }
            if (emailed){
                return "SUCCESS";
            }
            return "UNSUCCESSFUL";
        } catch (Exception e) {
            log.info(e);
            return "UNSUCCESSFUL";
        }
    }

}
