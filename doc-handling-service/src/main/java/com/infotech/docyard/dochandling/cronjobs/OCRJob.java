package com.infotech.docyard.dochandling.cronjobs;

import com.infotech.docyard.dochandling.service.DLDocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2
public class OCRJob {

    @Autowired
    private DLDocumentService dlDocumentService;

    @Scheduled(cron = "0 */10 * ? * *") //every 10 mins
    public void doOCRJob() {
        log.info("OCRJob - doOCRJob Job started at: " + LocalDateTime.now());
        try {
            Thread t = new Thread(new OCRThread(this.dlDocumentService));
            t.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Component
    public static class OCRThread implements Runnable {

        private final DLDocumentService dlDocumentService;

        public OCRThread(DLDocumentService dlDocumentService) {
            this.dlDocumentService = dlDocumentService;
        }

        @Override
        public void run() {
            System.out.println("OCRThread Started:  " + Thread.currentThread().getName());
            try {
                dlDocumentService.getContentFromAllDocuments();
            } catch (Exception exception) {
                System.out.println("getContentFromAllDocuments failed due to exception:  ");
                exception.printStackTrace();
            }
            System.out.println("OCRThread End: " + Thread.currentThread().getName());
        }
    }
}
