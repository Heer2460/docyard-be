package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentVersion;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentActivityRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentCommentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentVersionRepository;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentListDTO;
import com.infotech.docyard.dochandling.dto.UploadDocumentDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.enums.FileTypeEnum;
import com.infotech.docyard.dochandling.enums.FileViewerEnum;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;

@Service
@Log4j2
public class DLDocumentService {

    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private DLDocumentVersionRepository dlDocumentVersionRepository;
    @Autowired
    private DLDocumentCommentRepository dlDocumentCommentRepository;
    @Autowired
    private FTPService ftpService;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;
    @Autowired
    private RestTemplate restTemplate;

    public List<DLDocumentDTO> getDLDocumentsByFolderIdAndArchive(Long folderId, Boolean archived) {
        log.info("DLDocumentService - getDlDocumentsByFolderIdAndArchive method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findByParentIdIsNullAndArchivedOrderByUpdatedOnDesc(archived);
        } else {
            dlDocumentList = dlDocumentRepository.findByParentIdAndArchivedOrderByUpdatedOnDesc(folderId, archived);
        }
        for (DLDocument dlDoc : dlDocumentList) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(dlDoc, false);

            if (dlDoc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(dlDoc.getId());
                dto.setSize(fileCount + " Files");
            }
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

    public List<DLDocumentDTO> getDLDocumentsByOwnerIdFolderIdAndArchive(Long ownerId, Long folderId, Boolean archived) {
        log.info("DLDocumentService - getDlDocumentsByOwnerIdFolderIdAndArchive method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findByParentIdIsNullAndCreatedByAndArchivedOrderByUpdatedOnDesc(ownerId, archived);
        } else {
            dlDocumentList = dlDocumentRepository.findByCreatedByAndParentIdAndArchivedOrderByUpdatedOnDesc(ownerId, folderId, archived);
        }
        for (DLDocument dlDoc : dlDocumentList) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(dlDoc, false);

            if (dlDoc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(dlDoc.getId());
                dto.setSize(fileCount + " Files");
            }
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

    public List<DLDocumentDTO> getDocumentsByOwnerIdFolderIdAndArchive(Long ownerId, Long folderId, Boolean archived) {
        log.info("DLDocumentService - getDocumentsByOwnerIdFolderIdAndArchive method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findAllByAndCreatedByAndArchivedAndFolderFalseOrderByUpdatedOnDesc(ownerId, archived);
        } else {
            dlDocumentList = dlDocumentRepository.findAllByCreatedByAndParentIdAndArchivedAndFolderFalseOrderByUpdatedOnDesc(ownerId, folderId, archived);
        }
        for (DLDocument dlDoc : dlDocumentList) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(dlDoc, false);
            documentDTOList.add(dto);
        }
        return documentDTOList;
    }

    public String getUsedSpaceByUserId(Long ownerId) {
        log.info("DLDocumentService - getUsedSpaceByUserId method called...");

        List<DLDocument> dlDocumentList = dlDocumentRepository.findAllByCreatedByAndArchivedAndFolderFalseOrderByUpdatedOnDesc(ownerId, false);
        long usedSpace = dlDocumentList.stream().filter(d -> d.getSizeBytes() != null).mapToLong(DLDocument::getSizeBytes).sum();
        return DocumentUtil.getFileSize(usedSpace);
    }

    public List<DLDocumentDTO> getAllFavouriteDLDocumentsByOwnerIdFolderAndArchive(Long ownerId, Long folderId, Boolean archived) {
        log.info("DLDocumentService - getAllFavouriteDLDocumentsByOwnerIdFolderAndArchive method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findAllByCreatedByAndArchivedAndFavouriteTrueOrderByUpdatedOnDesc(ownerId, archived);
        } else {
            dlDocumentList = dlDocumentRepository.findByCreatedByAndParentIdAndArchivedAndFavouriteTrueOrderByUpdatedOnDesc(ownerId, folderId, archived);
        }
        for (DLDocument dlDoc : dlDocumentList) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(dlDoc, false);

            if (dlDoc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(dlDoc.getId());
                dto.setSize(fileCount + " Files");
            }
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

    public List<DLDocumentDTO> getAllFavouriteDLDocumentsByFolder(Long folderId) {
        log.info("DLDocumentService - getAllFavouriteDLDocumentsByFolder method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findAllByParentIdIsNullAndFavouriteOrderByUpdatedOnDesc(true);
        } else {
            dlDocumentList = dlDocumentRepository.findByParentIdAndFavouriteOrderByUpdatedOnDesc(folderId, true);
        }
        for (DLDocument dlDoc : dlDocumentList) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(dlDoc, false);

            if (dlDoc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(dlDoc.getId());
                dto.setSize(fileCount + " Files");
            }
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
        DLDocument dlDocument;
        if (optionalDLDocument.isPresent()) {
            dlDocument = optionalDLDocument.get();
            dlDocument.setFavourite(favourite);

            dlDocument = dlDocumentRepository.save(dlDocument);
            DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.UPLOADED.getValue(),
                    dlDocument.getId(), dlDocument.getId());
            activity.setCreatedOn(ZonedDateTime.now());
            dlDocumentActivityRepository.save(activity);
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        return dlDocument;
    }

    public DLDocument renameDLDocument(DLDocumentDTO dlDocumentDTO) {
        log.info("DLDocumentService - renameDLDocument method called...");

        Boolean alreadyExist = dlDocumentRepository.existsByName(dlDocumentDTO.getName());
        if (alreadyExist) {
            throw new DataValidationException(AppUtility.getResourceMessage("name.already.exist"));
        }
        Optional<DLDocument> optionalDLDocument = dlDocumentRepository.findById(dlDocumentDTO.getId());
        DLDocument dlDocument = null;
        if (optionalDLDocument.isPresent()) {
            dlDocument = optionalDLDocument.get();
            if (dlDocument.getFolder()) {
                dlDocument.setName(dlDocumentDTO.getName());
                dlDocument.setTitle(dlDocumentDTO.getName());
            } else {
                dlDocument.setTitle(dlDocumentDTO.getName());
                dlDocument.setName(dlDocumentDTO.getName());
            }
            dlDocument.setUpdatedBy(dlDocumentDTO.getUpdatedBy());
            dlDocument.setUpdatedOn(ZonedDateTime.now());
            dlDocument = dlDocumentRepository.save(dlDocument);
            DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getUpdatedBy(), DLActivityTypeEnum.RENAMED.getValue(),
                    dlDocument.getId(), dlDocument.getId());
            activity.setCreatedOn(ZonedDateTime.now());
            dlDocumentActivityRepository.save(activity);
        }
        return dlDocument;
    }

    public List<DLDocumentDTO> getAllRecentDLDocumentByOwnerId(Long ownerId) {
        log.info("DLDocumentService - getAllRecentDLDocumentByOwnerId method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        ZonedDateTime fromDate = ZonedDateTime.now().minusDays(7), toDate = ZonedDateTime.now();
        List<DLDocument> recentDocs = dlDocumentRepository.findTop8ByCreatedByAndArchivedFalseAndFolderFalseAndCreatedOnBetweenOrderByUpdatedOnDesc(ownerId, fromDate, toDate);
        for (DLDocument doc : recentDocs) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(doc, false);

            if (doc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(doc.getId());
                dto.setSize(fileCount + " Files");
            }
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

                // UPLOADING ON SFTP
                log.info("DLDocumentService - Uploaded on FTP started....");
                boolean isDocUploaded = ftpService.uploadFile(docLocation, dlDoc.getVersionGUId(), file.getInputStream());
                log.info("DLDocumentService - Uploaded on FTP ended with success: " + isDocUploaded);

                if (isDocUploaded) {
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

    public DLDocument createFolder(DLDocumentDTO folderRequestDTO) {
        log.info("DLDocumentService - createFolder method called...");

        Boolean folderAlreadyExist = dlDocumentRepository.existsByNameAndFolderTrue(folderRequestDTO.getName());
        if (folderAlreadyExist) {
            throw new DataValidationException(AppUtility.getResourceMessage("folder.name.already.exist"));
        }
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

    public void deleteDLDocument(DLDocumentListDTO dlDocumentIds) throws Exception {
        if (!AppUtility.isEmpty(dlDocumentIds)) {
            if (!AppUtility.isEmpty(dlDocumentIds.getDlDocumentIds())) {
                List<Long> dlDocIds = dlDocumentIds.getDlDocumentIds();
                for (Long id : dlDocIds) {
                    try {
                        deleteDLDocument(id);
                    } catch (Exception e) {
                        throw new DataValidationException(AppUtility.getResourceMessage("document.deleted.failure"));
                    }
                }
            }
        }
    }

    public void deleteDLDocument(Long dlDocumentId) throws Exception {
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
                            for (DLDocument dldoc : childDocs) {
                                deleteDLDocument(dldoc.getId());
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
            try{
                if (dlDocumentVersionRepository.existsByDlDocument_Id(docId)){
                    dlDocumentVersionRepository.deleteAllByDlDocument_Id(docId);
                }
                if (dlDocumentActivityRepository.existsByDocId(docId)){
                    dlDocumentActivityRepository.deleteAllByDocId(docId);
                }
                if (dlDocumentCommentRepository.existsByDlDocument_Id(docId)){
                    dlDocumentCommentRepository.deleteAllByDlDocument_Id(docId);
                }
                dlDocumentRepository.deleteById(docId);
            } catch (Exception e) {
                throw new DataValidationException(AppUtility.getResourceMessage("document.deleted.failure"));
            }
        }
    }

    public List<DLDocumentDTO> getAllTrashDLDocumentByOwnerId(Long ownerId) {
        log.info("DLDocumentService - getAllTrashDLDocumentByOwnerId method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> trashDlDocuments = dlDocumentRepository.findAllByArchivedTrueAndCreatedByOrderByUpdatedOnDesc(ownerId);
        for (DLDocument doc : trashDlDocuments) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(doc, false);

            if (doc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(doc.getId());
                dto.setSize(fileCount + " Files");
            }
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

    private StringBuffer getSelectedPath(DLDocument selectedFolderNode, String treeSelected,
                                         final String customPathSeparator) {
        log.info("DLDocumentService - getSelectedPath method called...");

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
        log.info("DLDocumentService - getDLDocumentById method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        if (opDoc.isPresent()) {
            DLDocumentDTO dlDocumentDTO = new DLDocumentDTO();
            dlDocumentDTO.convertToDTO(opDoc.get(), false);

            return dlDocumentDTO;
        }
        return new DLDocumentDTO();
    }

    public InputStreamResource downloadDLDocument(Long dlDocumentId) throws Exception {
        log.info("DLDocumentService - downloadDLDocument method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        InputStreamResource inputStreamResource = null;

        if (opDoc.isPresent()) {
            DLDocument doc = opDoc.get();
            if (!doc.getFolder()) {
                InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                inputStreamResource = new InputStreamResource(inputStream);

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.DOWNLOADED.getValue(),
                        doc.getId(), doc.getId());
                activity.setCreatedOn(ZonedDateTime.now());
                dlDocumentActivityRepository.save(activity);
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        return inputStreamResource;
    }

    public Boolean checkIsParent(Long dlDocumentId) {
        log.info("DLDocumentService - checkIsParent method called...");

        return !AppUtility.isEmpty(dlDocumentRepository.findByParentIdAndArchivedFalse(dlDocumentId));
    }

    public List<DLDocument> getChildren(Long dlDocumentId) {
        log.info("DLDocumentService - getChildren method called...");

        List<DLDocument> children = null;
        children = dlDocumentRepository.findByParentIdAndArchivedFalse(dlDocumentId);
        return children;
    }

    public synchronized void getContentFromAllDocuments() {
        log.info("DLDocumentService - getContentFromAllDocuments method called...");

        try {
            List<DLDocument> dlDocumentList = dlDocumentRepository.findAllByFolderFalseAndArchivedFalseAndOcrDoneFalseAndOcrSupportedTrue();
            for (DLDocument doc : dlDocumentList) {
                InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                if (!AppUtility.isEmpty(inputStream)) {
                    ITesseract instance = new Tesseract();
                    instance.setOcrEngineMode(1);
                    Path dataDirectory = Paths.get(ClassLoader.getSystemResource("tesseractdata").toURI());
                    instance.setDatapath(dataDirectory.toString());

                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    String result = instance.doOCR(bufferedImage);
                    doc.setContent(result);
                    doc.setOcrDone(true);
                    doc.setOcrSupported(true);
                    dlDocumentRepository.save(doc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            } else if (DocumentUtil.isOCRType(doc)) {
                /*ITesseract instance = new Tesseract();
                instance.setLanguage("eng");
                instance.setOcrEngineMode(1);
                Path dataDirectory = Paths.get(ClassLoader.getSystemResource("tesseractdata").toURI());
                instance.setDatapath(dataDirectory.toString());

                String result = instance.doOCR(f);
                doc.setContent(result);*/
                doc.setOcrDone(false);
                doc.setOcrSupported(true);
            } else if (doc.getExtension().equalsIgnoreCase(AppConstants.FileType.EXT_TXT)) {
                String data = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                doc.setContent(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String ocrDocument(MultipartFile file) {
        ITesseract instance = new Tesseract();
        String content = "";
        try {
            BufferedImage in = ImageIO.read(file.getInputStream());
            BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(in, 0, 0, null);
            g.dispose();
            instance.setDatapath("./testdata");
            content = instance.doOCR(newImage);
        } catch (TesseractException | IOException e) {
            log.error(e.getMessage());
        }
        return content;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void restoreArchivedDlDocument(DLDocumentListDTO docRestoreDTO) throws CustomException {
        if (AppUtility.isEmpty(docRestoreDTO)){
            throw new DataValidationException(AppUtility.getResourceMessage("document.ids.not.found"));
        }
        List dlDocumentIds = docRestoreDTO.getDlDocumentIds();
        if (dlDocumentIds.size() == 1) {
            try {
                DLDocument doc = dlDocumentRepository.findByIdAndArchivedTrue((Long) dlDocumentIds.get(0));
                if (AppUtility.isEmpty(doc)) {
                    throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
                }
                doc.setArchived(false);
                dlDocumentRepository.save(doc);
            } catch (Exception e) {
                ResponseUtility.exceptionResponse(e);
            }
        } else {
            for (Object docId : dlDocumentIds) {
                try {
                    DLDocument doc = dlDocumentRepository.findByIdAndArchivedTrue((Long) docId);
                    if (AppUtility.isEmpty(doc)) {
                        throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
                    }
                    doc.setArchived(false);
                    dlDocumentRepository.save(doc);
                } catch (Exception e) {
                    ResponseUtility.exceptionResponse(e);
                }
            }
        }
    }
}