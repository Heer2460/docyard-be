package com.infotech.docyard.service;

import com.infotech.docyard.dl.entity.ConfigSMTP;
import com.infotech.docyard.dl.entity.EmailInstance;
import com.infotech.docyard.dl.repository.ConfigSMTPRepository;
import com.infotech.docyard.dl.repository.EmailInstanceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Log4j2
public class EmailService {

    @Autowired
    private EmailInstanceRepository emailInstanceRepository;
    @Autowired
    private ConfigSMTPRepository configSMTPRepository;


    @Transactional(rollbackFor = {Throwable.class})
    public EmailInstance saveUpdateEmailInstance(EmailInstance emailInstance) {
        log.info("saveUpdateEmailInstance method called..");

        return emailInstanceRepository.save(emailInstance);
    }

    public List<EmailInstance> getAllByStatus(String status) {
        log.info("getAllByStatus method called..");

        return emailInstanceRepository.findByStatusContainingIgnoreCase(status);
    }

    public ConfigSMTP getConfigSMTP() {
        log.info("getConfigSMTP method called..");

        return configSMTPRepository.findFirstByOrderByCreatedOn();
    }

}