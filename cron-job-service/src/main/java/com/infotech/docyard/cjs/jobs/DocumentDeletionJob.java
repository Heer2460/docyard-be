package com.infotech.docyard.cjs.jobs;

import com.infotech.docyard.cjs.service.JobService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2
public class DocumentDeletionJob {
    @Autowired
    private JobService jobService;

    // @Scheduled(cron = "0 0 0 * * ?") // Start for every day.
    @Scheduled(cron = "0 0 * * * *") //Every hour
    public void deleteArchivedDocumentsJob() {
        log.info("DocumentDeletionJob - deleteArchivedDocumentsJob Job started at: " + LocalDateTime.now());
        try {
            Thread t = new Thread(new DocumentDeletionThread(this.jobService));
            t.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Component
    public static class DocumentDeletionThread implements Runnable {

        private final JobService dlDocumentService;

        public DocumentDeletionThread(JobService dlDocumentService) {
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
