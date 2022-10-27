package com.infotech.docyard.cjs.service;

import com.infotech.docyard.cjs.config.OCRProperties;
import com.infotech.docyard.cjs.dl.entity.DLDocument;
import com.infotech.docyard.cjs.dl.entity.DLDocumentActivity;
import com.infotech.docyard.cjs.dl.entity.ForgotPasswordLink;
import com.infotech.docyard.cjs.dl.repository.*;
import com.infotech.docyard.cjs.enums.DLActivityTypeEnum;
import com.infotech.docyard.cjs.exceptions.CustomException;
import com.infotech.docyard.cjs.exceptions.DataValidationException;
import com.infotech.docyard.cjs.util.AppUtility;
import com.infotech.docyard.cjs.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class JobService {

    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private FTPService ftpService;
    @Autowired
    private ForgotPasswordLinkRepository forgotPasswordLinkRepository;
    @Autowired
    private DLDocumentVersionRepository dlDocumentVersionRepository;
    @Autowired
    private DLDocumentCommentRepository dlDocumentCommentRepository;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;
    @Autowired
    private OCRProperties ocrProperties;

    public synchronized void getContentFromAllDocuments() {
        log.info("DLDocumentService - getContentFromAllDocuments method called...");

        try {
            List<DLDocument> dlDocumentList = dlDocumentRepository.findAllByFolderFalseAndArchivedFalseAndOcrDoneFalseAndOcrSupportedTrue();
            log.info("DLDocumentService - Document size is: " + dlDocumentList.size());

            for (DLDocument doc : dlDocumentList) {
                log.info("DLDocumentService - Document name is: " + doc.getName());

                if (AppUtility.isOCRType(doc)) {
                    log.info("DLDocumentService - Document OCR supported.");

                    InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                    if (!AppUtility.isEmpty(inputStream)) {
                        log.info("DLDocumentService - Input Stream fetched successfully.");

                        ITesseract instance = new Tesseract();
                        instance.setOcrEngineMode(1);
                        instance.setDatapath(ocrProperties.getPath());
                        log.info("DLDocumentService - Data Model directory path is: " + ocrProperties.getPath());

                        BufferedImage bufferedImage = ImageIO.read(inputStream);
                        String result = instance.doOCR(bufferedImage);
                        log.info("DLDocumentService - result is : " + result);

                        doc.setContent(result);
                        doc.setOcrDone(true);
                        doc.setOcrSupported(true);
                        dlDocumentRepository.save(doc);
                    } else {
                        log.info("DLDocumentService - Input Stream fetched failed.");
                    }
                }
            }
        } catch (Exception e) {
            log.info("DLDocumentService - Exception occurred.");

            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void deleteDLDocument(Long dlDocumentId) {
        log.info("DLDocumentService - deleteDocument method called...");

        if (!AppUtility.isEmpty(dlDocumentId)) {
            if (dlDocumentRepository.existsById(dlDocumentId)) {
                Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
                if (opDoc.isPresent()) {
                    DLDocument dlDocument = opDoc.get();
                    Boolean parent = checkIsParent(dlDocumentId);
                    if (parent) {
                        List<DLDocument> childDocs = getChildren(dlDocumentId);
                        if (!(childDocs == null || AppUtility.isEmpty(childDocs))) {
                            for (DLDocument dlDoc : childDocs) {
                                deleteDLDocument(dlDoc.getId());
                            }
                        }
                    }
                    try {
                        deleteFileWithDependencies(dlDocument);
                    } catch (Exception e) {
                        throw new DataValidationException(AppUtility.getResourceMessage("document.deleted.failure"));
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void deleteFileWithDependencies(DLDocument dldocument) throws Exception {
        boolean deleted = false;
        Long docId = dldocument.getId();
        if (!dldocument.getFolder()) {
            deleted = ftpService.deleteFile(dldocument.getLocation(), dldocument.getVersionGUId());
        }
        if (deleted || dldocument.getFolder()) {
            try {
                if (dlDocumentVersionRepository.existsByDlDocument_Id(docId)) {
                    dlDocumentVersionRepository.deleteAllByDlDocument_Id(docId);
                }
                if (dlDocumentCommentRepository.existsByDlDocument_Id(docId)) {
                    dlDocumentCommentRepository.deleteAllByDlDocument_Id(docId);
                }
                DLDocumentActivity activity = new DLDocumentActivity(dldocument.getCreatedBy(), DLActivityTypeEnum.FILE_DELETED.getValue(),
                        dldocument.getId(), dldocument.getId());
                dlDocumentActivityRepository.save(activity);
                dlDocumentRepository.deleteById(docId);
            } catch (Exception e) {
                throw new DataValidationException(AppUtility.getResourceMessage("document.deleted.failure"));
            }
        }
    }

    public List<DLDocument> getChildren(Long dlDocumentId) {
        log.info("DLDocumentService - getChildren method called...");

        List<DLDocument> children = null;
        children = dlDocumentRepository.findByParentIdAndArchivedFalse(dlDocumentId);
        return children;
    }

    public Boolean checkIsParent(Long dlDocumentId) {
        log.info("DLDocumentService - checkIsParent method called...");

        return !AppUtility.isEmpty(dlDocumentRepository.findByParentIdAndArchivedFalse(dlDocumentId));
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void deleteArchivedDocuments() throws CustomException {
        List<DLDocument> archivedDLDocs = dlDocumentRepository.findAllByArchivedTrue();
        if (!AppUtility.isEmpty(archivedDLDocs)) {
            try {
                for (DLDocument archivedDoc : archivedDLDocs) {
                    if (archivedDoc.getDaysArchived() != null && !AppUtility.isEmpty(archivedDoc.getDaysArchived())) {
                        archivedDoc.setDaysArchived(archivedDoc.getDaysArchived() + 1);
                        if (archivedDoc.getDaysArchived() >= 30) {
                            deleteDLDocument(archivedDoc.getId());
                        } else {
                            dlDocumentRepository.save(archivedDoc);
                        }
                    }
                }
            } catch (Exception e) {
                ResponseUtility.exceptionResponse(e);
            }
        }
    }

    public void expireForgotPasswordLinks() {
        log.info("expireForgotPasswordLinks called..");

        List<ForgotPasswordLink> forgotPasswordLinkList = forgotPasswordLinkRepository.findAllByTokenIsNotNull();

        for (ForgotPasswordLink forgotPasswordLink : forgotPasswordLinkList) {
            if (forgotPasswordLink.getCreatedOn().plusMinutes(30).isBefore(ZonedDateTime.now())) {
                forgotPasswordLink.setToken(null);
                forgotPasswordLink.setExpired(true);
                forgotPasswordLink.setExpiredOn(ZonedDateTime.now());
                forgotPasswordLinkRepository.save(forgotPasswordLink);
            }
        }
    }
}
