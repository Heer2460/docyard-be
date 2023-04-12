package com.infotech.docyard.cjs.jobs;


import com.infotech.docyard.cjs.service.JobService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2
public class DlDocumentCheckOutJob {

    @Autowired
    private JobService jobService;


    @Scheduled(cron = "0 */5 * ? * *") //every 30 mins
    public void dlDocumentCheckOutJob() {
        log.info("DlDocumentCheckOutJob - dlDocumentCheckOut Job started at: " + LocalDateTime.now());
        try {
            Thread t = new Thread(new DlDocumentCheckOutJobThread(this.jobService));
            t.start();
            log.info("DlDocumentCheckOutJob - dlDocumentCheckOut Job ended at: " + LocalDateTime.now());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Component
    public static class DlDocumentCheckOutJobThread implements Runnable {

        private final JobService jobservice;

        public DlDocumentCheckOutJobThread(JobService dlDocumentService) {
            this.jobservice = dlDocumentService;
        }

        @Override
        public void run() {
            System.out.println("DlDocumentCheckOutJobThread Started:  " + Thread.currentThread().getName());
            try {
                log.info("checkInCheckOutDLDocument method calling started...");
                jobservice.checkInCheckOutDLDocument();
            } catch (Exception exception) {
                log.info("checkInCheckOutDLDocument failed due to exception...");
                exception.printStackTrace();
            }
            log.info("DlDocumentCheckOutJobThread End: " + Thread.currentThread().getName());
        }
    }
}
