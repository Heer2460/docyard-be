package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentVersion;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentActivityRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentVersionRepository;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.dto.UploadDocumentDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.enums.FileTypeEnum;
import com.infotech.docyard.dochandling.enums.FileViewerEnum;
import com.infotech.docyard.dochandling.util.AppConstants;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DocumentUtil;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

@Service
@Log4j2
public class DLDocumentService {

    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private DLDocumentVersionRepository dlDocumentVersionRepository;
    @Autowired
    private FTPService ftpService;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;
    @Autowired
    private RestTemplate restTemplate;

    public List<DLDocumentDTO> getDocumentsByFolderIdAndArchive(Long folderId, Boolean archived) {
        log.info("DLDocumentService - getDocumentsByFolderIdAndArchive method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findByParentIdIsNullAndArchivedOrderByUpdatedOnAsc(archived);
        } else {
            dlDocumentList = dlDocumentRepository.findByParentIdAndArchivedOrderByUpdatedOnAsc(folderId, archived);
        }
        for (DLDocument dlDoc : dlDocumentList) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(dlDoc, false);

            Object response = restTemplate.getForObject("http://um-service/um/user/" + dlDoc.getCreatedBy(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                dto.setCreatedByName((String) map.get("name"));
                dto.setUpdatedByName((String) map.get("name"));
            }
            documentDTOList.add(dto);
        }
        return documentDTOList;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocument updateFavourite(Long dlDocumentId, Boolean favourite) {
        log.info("DLDocumentService - updateFavourite method called...");

        Optional<DLDocument> optionalDLDocument = dlDocumentRepository.findById(dlDocumentId);
        DLDocument dlDocument = null;
        if (!AppUtility.isEmpty(optionalDLDocument)) {
            dlDocument = optionalDLDocument.get();
            dlDocument.setFavourite(favourite);
        }
        dlDocument = dlDocumentRepository.save(dlDocument);
        DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.UPLOADED.getValue(),
                dlDocument.getId(), dlDocument.getId());
        activity.setCreatedOn(ZonedDateTime.now());
        dlDocumentActivityRepository.save(activity);
        return dlDocument;
    }

    public List<DLDocumentDTO> getAllRecentDLDocumentByOwnerId(Long ownerId) {
        log.info("DLDocumentService - getAllRecentDLDocumentByOwnerId method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        ZonedDateTime fromDate = ZonedDateTime.now().minusDays(7), toDate = ZonedDateTime.now();
        List<DLDocument> recentDocs = dlDocumentRepository.findTop8ByCreatedByAndArchivedFalseAndFolderFalseAndCreatedOnBetweenOrderByUpdatedOnAsc(ownerId, fromDate, toDate);
        for (DLDocument doc : recentDocs) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(doc, false);

            Object response = restTemplate.getForObject("http://um-service/um/user/" + ownerId, Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                dto.setCreatedByName((String) map.get("name"));
                dto.setUpdatedByName((String) map.get("name"));
            }
            documentDTOList.add(dto);
        }
        return documentDTOList;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocument uploadDocuments(UploadDocumentDTO uploadDocumentDTO, MultipartFile[] files) throws Exception {
        log.info("DLDocumentService - uploadDocuments method called...");

        DLDocument dlDoc = null;
        if (files.length > 0) {
            for (MultipartFile file : files) {
                log.info("DLDocumentService - buildDocument object started...");
                dlDoc = this.buildDocument(file, uploadDocumentDTO, true);
                log.info("DLDocumentService - buildDocument object ended...");

                String docLocation = dlDoc.getLocation();
                log.info("DLDocumentService - DocumentResponseWrapper locations is:" + docLocation);

                log.info("DLDocumentService - Creating temp file to upload....");
                File f = File.createTempFile(docLocation, dlDoc.getExtension());
                f.deleteOnExit();
                FileUtils.writeByteArrayToFile(f, file.getBytes());

                // Getting content from file
                getContentFromFile(dlDoc, f);

                // UPLOADING ON SFTP
                log.info("DLDocumentService - Uploaded on FTP started....");
                boolean isDocUploaded = ftpService.uploadFile(docLocation, dlDoc.getVersionGUId(), file.getInputStream());
                log.info("DLDocumentService - Uploaded on FTP ended with success: " + isDocUploaded);

                if (isDocUploaded) {
                    dlDoc.setContent(getDocumentContent(file));
                    dlDoc = dlDocumentRepository.save(dlDoc);
                    DLDocumentActivity activity = new DLDocumentActivity(dlDoc.getCreatedBy(), DLActivityTypeEnum.UPLOADED.getValue(),
                            dlDoc.getId(), dlDoc.getId());
                    activity.setCreatedOn(ZonedDateTime.now());
                    dlDocumentActivityRepository.save(activity);
                }
            }
        }
        return dlDoc;
    }


    public DLDocumentVersion createNewDocumentVersion(DLDocument document, Long userId) {
        log.info("DLDocumentService - createNewDocumentVersion method called...");

        DLDocumentVersion dv = new DLDocumentVersion();
        dv.setGuId(UUID.randomUUID().toString());
        dv.setDlDocument(document);
        dv.setKeyString(document.getName());
        dv.setCreatedOn(ZonedDateTime.now());
        dv.setUpdatedOn(ZonedDateTime.now());
        dv.setVisible(true);
        dv.setVersion(AppConstants.FIRST_VERSION);
        dv.setUserId(userId);
        return dv;
    }

    @Transactional(rollbackFor = {Throwable.class})
    protected DLDocument buildDocument(MultipartFile file, UploadDocumentDTO request, boolean isDocUpload) {
        DLDocument doc = new DLDocument();

        try {
            String title = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
            doc.setTitle(title);

            String fileName = file.getOriginalFilename().replaceAll(" ", "_");
            doc.setName(fileName);

            int extDot = file.getOriginalFilename().lastIndexOf('.');
            String extension = extDot > 0 ? file.getOriginalFilename().substring(extDot + 1) : "";
            doc.setExtension(extension);
            doc.setMimeType(DocumentUtil.getMimeType(extension));
            doc.setSize(DocumentUtil.getFileSize(file.getSize()));
            doc.setSizeBytes(file.getSize());
            doc.setCreatedBy(request.getCreatedBy());
            doc.setUpdatedBy(request.getCreatedBy());
            doc.setCreatedOn(ZonedDateTime.now());
            doc.setUpdatedOn(ZonedDateTime.now());
            if (request.getFolderId() != 0) {
                doc.setParentId(request.getFolderId());
            }
            doc.setFolder(false);
            doc = dlDocumentRepository.save(doc);
            doc.setArchived(false);
            DLDocument folder = dlDocumentRepository.findByIdAndArchivedFalseAndFolderTrue(request.getFolderId());
            String selectedFolderPath = this.getNodePath(folder).toString();
            doc.setLocation(selectedFolderPath + "/");
            doc.setCurrentVersion(AppConstants.FIRST_VERSION);
            doc.setVersion(AppConstants.FIRST_VERSION);
            doc.setCreatedOn(ZonedDateTime.now());
            doc.setDocumentVersions(new ArrayList<>());
            DLDocumentVersion documentVersion = createNewDocumentVersion(doc, request.getCreatedBy());
            documentVersion = dlDocumentVersionRepository.save(documentVersion);

            doc.getDocumentVersions().add(documentVersion);

            if (!isDocUpload) {
                if (AppConstants.FileType.EXT_HTML.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_HTML);
                    doc.setExtension(AppConstants.FileType.EXT_HTML);
                    doc.setMimeType(AppConstants.MimeType.MIME_HTML);
                } else if (AppConstants.FileType.EXT_DOC.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_DOC);
                    doc.setExtension(AppConstants.FileType.EXT_DOC);
                    doc.setMimeType(AppConstants.MimeType.MIME_DOC);
                } else if (AppConstants.FileType.EXT_DOCX.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_DOCX);
                    doc.setExtension(AppConstants.FileType.EXT_DOCX);
                    doc.setMimeType(AppConstants.MimeType.MIME_DOCX);
                } else if (AppConstants.FileType.EXT_XLS.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_XLS);
                    doc.setExtension(AppConstants.FileType.EXT_XLS);
                    doc.setMimeType(AppConstants.MimeType.MIME_XLS);
                } else if (AppConstants.FileType.EXT_XLSX.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_XLSX);
                    doc.setExtension(AppConstants.FileType.EXT_XLSX);
                    doc.setMimeType(AppConstants.MimeType.MIME_XLSX);
                } else if (AppConstants.FileType.EXT_PPT.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_PPT);
                    doc.setExtension(AppConstants.FileType.EXT_PPT);
                    doc.setMimeType(AppConstants.MimeType.MIME_PPT);
                } else if (AppConstants.FileType.EXT_PPTX.equals(extension)) {
                    doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                            + AppConstants.FileType.EXT_PPTX);
                    doc.setExtension(AppConstants.FileType.EXT_PPTX);
                    doc.setMimeType(AppConstants.MimeType.MIME_PPTX);
                }
            } else {
                doc.setExtension(extension.toLowerCase());
                doc.setMimeType(DocumentUtil.getMimeType(extension.toLowerCase()));
            }
            doc.setVersionGUId(documentVersion.getGuId() + "."
                    + doc.getExtension());
            documentVersion.setGuId(documentVersion.getGuId() + "."
                    + doc.getExtension());

            if (FileTypeEnum.getByExtensionForcefully(doc.getExtension()).getSupportedViewer() == FileViewerEnum.IMAGE_VIEWER) {//doc.setThumbnailSupported(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    private StringBuffer getNodePath(DLDocument selectedFolderNode) {
        return getSelectedPath(selectedFolderNode,
                "0", null);
    }

    private void getContentFromFile(DLDocument doc, File f) {

        FileInputStream fileInputStream = null;
        try {
            if (doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_DOCX)) {
                XWPFDocument document = null;

                fileInputStream = new FileInputStream(f);
                document = new XWPFDocument(fileInputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);

                doc.setContent(extractor.getText());
            } else if (doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_DOC)) {
                // it needs to implement
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public DLDocument createFolder(DLDocumentDTO folderRequestDTO) {
        log.info("DLDocumentService - createFolder method called...");

        DLDocument folder = new DLDocument();
        folder.setName(folderRequestDTO.getName().trim());
        folder.setTitle(folderRequestDTO.getTitle().trim());
        folder.setFolder(true);
        folder.setArchived(false);
        folder.setArchivedOn(null);
        folder.setParentId(folderRequestDTO.getParentId());
        folder.setLeafNode(false);
        folder.setCreatedBy(folderRequestDTO.getCreatedBy());
        folder.setCreatedOn(ZonedDateTime.now());
        folder.setUpdatedBy(folderRequestDTO.getUpdatedBy());
        folder.setUpdatedOn(ZonedDateTime.now());
        folder = dlDocumentRepository.save(folder);

        DLDocumentActivity activity = new DLDocumentActivity(folder.getCreatedBy(), DLActivityTypeEnum.CREATED.getValue(),
                folder.getId(), folder.getId());
        activity.setCreatedBy(folderRequestDTO.getCreatedBy());
        activity.setCreatedOn(ZonedDateTime.now());
        activity.setUpdatedBy(folderRequestDTO.getUpdatedBy());
        activity.setUpdatedOn(ZonedDateTime.now());
        dlDocumentActivityRepository.save(activity);
        return folder;
    }

    public DLDocument archiveDlDocument(Long dlDocumentId, Boolean archive) {
        log.info("archiveDlDocument method called..");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        DLDocument doc = null;
        if (opDoc.isPresent()) {
            doc = opDoc.get();
            doc.setArchived(archive);
            dlDocumentRepository.save(doc);
        }
        DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.ARCHIVED.getValue(),
                doc.getId(), doc.getId());
        activity.setCreatedOn(ZonedDateTime.now());
        dlDocumentActivityRepository.save(activity);
        return doc;
    }

    public void deleteDLDocument(Long dlDocumentId) throws Exception {
        log.info("DLDocumentService - deleteDocument method called...");

        DLDocument dlDoc = null;
        Optional<DLDocument> optionalDLDoc = dlDocumentRepository.findById(dlDocumentId);
        try {
            dlDoc = optionalDLDoc.get();
            String docLocation = dlDoc.getLocation();
            log.info("DLDocumentService - Deletion on FTP started....");
            if (!dlDoc.getFolder()) {
                boolean isDocDeleted = ftpService.deleteFile(docLocation, dlDoc.getVersionGUId());
                log.info("DLDocumentService - Deletion on FTP ended with success: " + isDocDeleted);
                if (isDocDeleted) {
                    dlDocumentRepository.deleteById(dlDocumentId);
                }
            } else {
                boolean isDocDeleted = ftpService.deleteDirectory(docLocation, dlDoc.getVersionGUId());
                log.info("DLDocumentService - Deletion on FTP ended with success: " + isDocDeleted);
                if (isDocDeleted) {
                    dlDocumentRepository.deleteById(dlDocumentId);
                }
            }
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
    }

    private StringBuffer getSelectedPath(DLDocument selectedFolderNode, String treeSelected, final String customPathSeparator) {
        StringBuilder selectedFolderPath = new StringBuilder();
        final String PATH_SEPARATOR = DocumentUtil.buildPathSeparator(customPathSeparator);
        DLDocument folder = selectedFolderNode;
        if (DocumentUtil.isRootFolder(folder)) {
            selectedFolderPath = new StringBuilder("Root");
            return new StringBuffer(selectedFolderPath);
        }

        while (!AppUtility.isEmpty(folder)) {
            if (folder.getId() == null) {
                selectedFolderPath.insert(0, PATH_SEPARATOR);
            } else {
                selectedFolderPath.insert(0, folder.getName() + PATH_SEPARATOR);
            }
            folder.setShared(folder.getShared());
            treeSelected = treeSelected != null ? treeSelected : "0";
            folder = dlDocumentRepository.findByIdAndArchivedFalseAndFolderTrue(folder.getParentId());
        }
        int length = selectedFolderPath.length();
        selectedFolderPath.setLength(length > 0 ? length - PATH_SEPARATOR.length() : length);

        return new StringBuffer(selectedFolderPath);
    }

    public DLDocumentDTO getDLDocumentById(Long dlDocumentId) {
        log.info("getDLDocumentById method called..");
        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        if (opDoc.isPresent()) {
            DLDocumentDTO dlDocumentDTO = new DLDocumentDTO();
            dlDocumentDTO.convertToDTO(opDoc.get(), false);

            return dlDocumentDTO;
        }

        return new DLDocumentDTO();
    }

    public String getDocumentContent(MultipartFile file ){
        ITesseract instance = new Tesseract();
        String content = "";
        try {

            BufferedImage in = ImageIO.read(file.getInputStream());
            BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(in, 0, 0, null);
            g.dispose();
            instance.setDatapath("./tessdata");
            content = instance.doOCR(newImage);



        } catch (TesseractException | IOException e) {
            System.err.println(e.getMessage());

        }
        return content;
    }
}