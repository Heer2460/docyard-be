package com.infotech.docyard.dochandling.cronjobs;

import com.infotech.docyard.dochandling.service.DLDocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2

public class DocumentDeletionJob {
    @Autowired
    private DLDocumentService dlDocumentService;

    @Scheduled(cron = "0 55 23 * * *") //Every day at 23:55
    public void deleteArchivedDocumentsJob() {
        log.info("DocumentDeletionJob - deleteArchivedDocumentsJob Job started at: " + LocalDateTime.now());
        try {
            Thread t = new Thread(new DocumentDeletionThread(this.dlDocumentService));
            t.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Component
    public static class DocumentDeletionThread implements Runnable {

        private final DLDocumentService dlDocumentService;

        public DocumentDeletionThread(DLDocumentService dlDocumentService) {
            this.dlDocumentService = dlDocumentService;
        }

        @Override
        public void run() {
            log.info("DocumentDeletionThread Started:  " + Thread.currentThread().getName());
            try {
                dlDocumentService.deleteArchivedDocuments();
            } catch (Exception exception) {
                log.info("deleteArchivedDocuments failed due to exception:  ");
                exception.printStackTrace();
            }
            log.info("DocumentDeletionThread End: " + Thread.currentThread().getName());
        }
    }
}
