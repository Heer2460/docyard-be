package com.infotech.docyard.cjs.jobs;


import com.infotech.docyard.cjs.service.JobService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2
public class OCRJob {

    @Autowired
    private JobService jobService;

    @Scheduled(cron = "0 */10 * ? * *") //every 10 mins
    public void doOCRJob() {
        log.info("OCRJob - doOCRJob Job started at: " + LocalDateTime.now());
        try {
            Thread t = new Thread(new OCRThread(this.jobService));
            t.start();
            log.info("OCRJob - doOCRJob Job ended at: " + LocalDateTime.now());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Component
    public static class OCRThread implements Runnable {

        private final JobService jobservice;

        public OCRThread(JobService dlDocumentService) {
            this.jobservice = dlDocumentService;
        }

        @Override
        public void run() {
            System.out.println("OCRThread Started:  " + Thread.currentThread().getName());
            try {
                System.out.println("getContentFromAllDocuments method calling started...");
                jobservice.getContentFromAllDocuments();
            } catch (Exception exception) {
                System.out.println("getContentFromAllDocuments failed due to exception...");
                exception.printStackTrace();
            }
            System.out.println("OCRThread End: " + Thread.currentThread().getName());
        }
    }
}
