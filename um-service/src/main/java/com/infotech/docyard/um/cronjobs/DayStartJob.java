package com.infotech.docyard.um.cronjobs;

import com.infotech.docyard.um.service.UserService;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class DayStartJob {

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 * * * * *") // Each Minute
    public void eachMinuteJob() {
        log.info("eachMinuteJob called..");
        userService.expireForgotPasswordLinks();
    }

}
