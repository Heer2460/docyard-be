package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.*;
import com.infotech.docyard.dochandling.dl.repository.*;
import com.infotech.docyard.dochandling.dto.*;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.enums.FileTypeEnum;
import com.infotech.docyard.dochandling.enums.FileViewerEnum;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import com.infotech.docyard.dochandling.util.AppConstants;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DocumentUtil;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private DLCollaboratorRepository dlCollaboratorRepository;
    @Autowired
    private DLShareCollaboratorRepository dlShareCollaboratorRepository;
    @Autowired
    private DLShareRepository dlShareRepository;
    @Autowired
    private FTPService ftpService;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    HttpServletResponse response;
    List<String> filesListInDir = new ArrayList<String>();

    public List<DLDocumentDTO> searchDLDocuments(String searchKey, Long userId) {
        log.info("DLDocumentService - searchDLDocuments method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList = dlDocumentRepository.findDLDocumentBySearchKey(searchKey, userId);
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

    public List<DLDocumentDTO> getDLDocumentHierarchy(Long dlDocId) {
        log.info("DLDocumentService - getDLDocumentHierarchy method called...");

        List<DLDocumentDTO> docDTOList = new ArrayList<>();
        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocId);
        if (!opDoc.isPresent()) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        } else {
            DLDocument doc = opDoc.get();
            if (doc.getParentId() != null && !AppUtility.isEmpty(doc.getParentId())) {
                docDTOList = getDLDocumentHierarchy(doc.getParentId());
            }
            DLDocumentDTO docDTO = new DLDocumentDTO();
            docDTO.convertToDTO(doc, true);
            docDTOList.add(docDTO);
        }
        return docDTOList;
    }

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

    public List<DLDocumentDTO> getAllDocumentsByFolderAndArchive(Long folderId, Boolean archived) {
        log.info("DLDocumentService - getAllDocumentsByFolderAndArchive method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> dlDocumentList;
        if (AppUtility.isEmpty(folderId) || folderId == 0L) {
            dlDocumentList = dlDocumentRepository.findByParentIdIsNullAndArchivedAndFolderFalseOrderByUpdatedOnDesc(archived);
        } else {
            dlDocumentList = dlDocumentRepository.findByParentIdAndArchivedAndFolderFalseOrderByUpdatedOnDesc(folderId, archived);
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

    public List<DLDocumentDTO> getAllDlDocumentsByFolderId(Long folderId, Boolean shared) {
        log.info("DLDocumentService - getAllDlDocumentsByFolderId method called...");

        DLDocument dlFolder;
        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        if (shared) {
            dlFolder = dlDocumentRepository.findByIdAndArchivedFalseAndFolderTrueAndSharedTrue(folderId);

            if (AppUtility.isEmpty(dlFolder)) {
                throw new NoDataFoundException(AppUtility.getResourceMessage("folder.not.shared"));
            } else {
                List<DLDocument> dlDocumentList = dlDocumentRepository.findByParentIdAndArchivedFalseOrderByUpdatedOnDesc(folderId);
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
            }
        } else {
            List<DLDocument> dlDocumentList = dlDocumentRepository.findByParentIdAndArchivedFalseOrderByUpdatedOnDesc(folderId);
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

    public UserSpaceOccupiedDTO getUsedSpaceByUserId(Long ownerId) {
        log.info("DLDocumentService - getUsedSpaceByUserId method called...");

        List<DLDocument> dlDocumentList = dlDocumentRepository.findAllByCreatedByAndArchivedAndFolderFalseOrderByUpdatedOnDesc(ownerId, false);
        long usedSpace = dlDocumentList.stream().filter(d -> d.getSizeBytes() != null).mapToLong(DLDocument::getSizeBytes).sum();
        return new UserSpaceOccupiedDTO(usedSpace, DocumentUtil.getFileSize(usedSpace));
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

    public List<DLDocumentDTO> getSharedByMeDLDocumentsByFolder(Long userId, Long folderId) {
        log.info("DLDocumentService - getSharedByMeDLDocuments method called...");

        List<DLDocument> dlDocumentList = null;
        List<DLDocumentDTO> dlDocumentDTOList = new ArrayList<>();
        if (!AppUtility.isEmpty(userId)) {
            if (AppUtility.isEmpty(folderId) || folderId == 0L) {
                dlDocumentList = dlDocumentRepository.findAllByCreatedByAndSharedTrueAndArchivedFalseOrderByUpdatedOnDesc(userId);
            } else {
                dlDocumentList = dlDocumentRepository.findByCreatedByAndParentIdAndArchivedFalseOrderByUpdatedOnDesc(userId, folderId);
            }
            for (DLDocument d : dlDocumentList) {
                DLDocumentDTO docDTO = new DLDocumentDTO();
                docDTO.convertToDTO(d, true);

                if (d.getFolder()) {
                    int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(d.getId());
                    docDTO.setSize(fileCount + " Files");
                }
                Object response = restTemplate.getForObject("http://um-service/um/user/" + d.getCreatedBy(), Object.class);
                if (!AppUtility.isEmpty(response)) {
                    HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                    docDTO.setCreatedByName((String) map.get("name"));
                    docDTO.setUpdatedByName((String) map.get("name"));
                }
                dlDocumentDTOList.add(docDTO);
            }
        }
        return dlDocumentDTOList;
    }

    public List<DLDocumentDTO> getSharedWithMeDLDocuments(Long userId, Long folderId) {
        log.info("DLDocumentService - getSharedWithMeDLDocuments method called...");

        List<DLDocument> dlDocumentList = new ArrayList<>();
        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        String email = null;
        if (!AppUtility.isEmpty(userId)) {
            Object responseEmails = restTemplate.getForObject("http://um-service/um/user/" + userId, Object.class);
            if (!AppUtility.isEmpty(responseEmails)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) responseEmails).get("data");
                email = (String) map.get("email");
            }
            DLCollaborator collaborator = dlCollaboratorRepository.findByEmail(email);
            if (!AppUtility.isEmpty(collaborator)) {
                List<DLShareCollaborator> shareColList = dlShareCollaboratorRepository.findAllByDlCollaboratorId(collaborator.getId());
                for (DLShareCollaborator shareCol : shareColList) {
                    Optional<DLShare> shareOp = dlShareRepository.findById(shareCol.getDlShareId());
                    if (shareOp.isPresent()) {
                        if (!AppUtility.isEmpty(shareOp.get().getDlDocumentId())) {
                            if (AppUtility.isEmpty(folderId) || folderId == 0L) {
                                Optional<DLDocument> opDoc = dlDocumentRepository.findById(shareOp.get().getDlDocumentId());
                                if (opDoc.isPresent()) {
                                    DLDocumentDTO dto = new DLDocumentDTO();
                                    dto.convertToDTO(opDoc.get(), true);
                                    Object responseNames = restTemplate.getForObject("http://um-service/um/user/" + dto.getCreatedBy(), Object.class);
                                    if (!AppUtility.isEmpty(responseNames)) {
                                        HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) responseNames).get("data");
                                        dto.setCreatedByName((String) map.get("name"));
                                        dto.setUpdatedByName((String) map.get("name"));
                                    }
                                    documentDTOList.add(dto);
                                }
                            } else {
                                Optional<DLDocument> optionalDLDocument = dlDocumentRepository.findById(folderId);
                                if (optionalDLDocument.isPresent()) {
                                    if (optionalDLDocument.get().getFolder()) {
                                        if (Objects.equals(shareOp.get().getDlDocumentId(), folderId)) {
                                            dlDocumentList = dlDocumentRepository.findByCreatedByAndParentIdAndArchivedFalseOrderByUpdatedOnDesc(
                                                    optionalDLDocument.get().getCreatedBy(), folderId);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (DLDocument doc : dlDocumentList) {
                    DLDocumentDTO docDTO = new DLDocumentDTO();
                    docDTO.convertToDTO(doc, true);
                    Object responseNames = restTemplate.getForObject("http://um-service/um/user/" + docDTO.getCreatedBy(), Object.class);
                    if (!AppUtility.isEmpty(responseNames)) {
                        HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) responseNames).get("data");
                        docDTO.setCreatedByName((String) map.get("name"));
                        docDTO.setUpdatedByName((String) map.get("name"));
                    }
                    documentDTOList.add(docDTO);
                }
            }
        }
        return documentDTOList;
    }

    public DashboardDTO getDashboardStats(Long userId) {
        log.info("DLDocumentService - getDashboardStats method called...");

        DashboardDTO dashboardDTO = null;
        if (!AppUtility.isEmpty(userId)) {
            List<DLDocument> docList = dlDocumentRepository.findAllByCreatedByAndArchivedFalse(userId);
            List<DLDocument> images;
            List<DLDocument> docs;
            List<DLDocument> videos;
            List<DLDocument> others;
            Long size = 0L;
            int count = 0;
            if (!AppUtility.isEmpty(docList)) {
                images = docList.stream().filter(this::isImage).collect(Collectors.toList());
                docs = docList.stream().filter(this::isDoc).collect(Collectors.toList());
                videos = docList.stream().filter(this::isVideo).collect(Collectors.toList());
                others = docList.stream().filter(doc -> (!isFolder(doc) && !isImage(doc) && !isDoc(doc) && !isVideo(doc))).collect(Collectors.toList());
                for (DLDocument vid : videos) {
                    size += vid.getSizeBytes();
                    count++;
                }
                DashboardDTO.VideosProps videosProps = new DashboardDTO.VideosProps(count, size, null);
                size = 0L;
                count = 0;
                for (DLDocument doc : docs) {
                    size += doc.getSizeBytes();
                    count++;
                }
                DashboardDTO.DocsProps docsProps = new DashboardDTO.DocsProps(count, size, null);
                size = 0L;
                count = 0;
                for (DLDocument img : images) {
                    size += img.getSizeBytes();
                    count++;
                }
                DashboardDTO.ImageProps imageProps = new DashboardDTO.ImageProps(count, size, null);
                size = 0L;
                count = 0;
                for (DLDocument other : others) {
                    size += other.getSizeBytes();
                    count++;
                }
                DashboardDTO.OthersProps othersProps = new DashboardDTO.OthersProps(count, size, null);
                dashboardDTO = new DashboardDTO(imageProps, videosProps, docsProps, othersProps);
            } else {
                DashboardDTO.VideosProps videosProps = new DashboardDTO.VideosProps(0, size, null);
                DashboardDTO.DocsProps docsProps = new DashboardDTO.DocsProps(0, size, null);
                DashboardDTO.ImageProps imageProps = new DashboardDTO.ImageProps(0, size, null);
                DashboardDTO.OthersProps othersProps = new DashboardDTO.OthersProps(0, size, null);

                dashboardDTO = new DashboardDTO(imageProps, videosProps, docsProps, othersProps);
            }
        }
        return dashboardDTO;
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
            DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.STARRED.getValue(),
                    dlDocument.getId(), dlDocument.getId());
            dlDocumentActivityRepository.save(activity);
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        return dlDocument;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocument renameDLDocument(DLDocumentDTO dlDocumentDTO) {
        log.info("DLDocumentService - renameDLDocument method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentDTO.getId());
        DLDocument doc = null;
        if (!opDoc.isPresent()) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        } else {
            doc = opDoc.get();
        }
        Boolean alreadyExist = dlDocumentRepository.existsByName(dlDocumentDTO.getName() + '.' + doc.getExtension());
        if (alreadyExist) {
            throw new DataValidationException(AppUtility.getResourceMessage("name.already.exist"));
        }
        if (doc.getFolder()) {
            doc.setName(dlDocumentDTO.getName());
            doc.setTitle(dlDocumentDTO.getName());
        } else {
            doc.setTitle(dlDocumentDTO.getName());
            doc.setName(dlDocumentDTO.getName() + "." + doc.getExtension());
        }
        doc.setUpdatedBy(dlDocumentDTO.getUpdatedBy());
        doc.setUpdatedOn(ZonedDateTime.now());
        doc = dlDocumentRepository.save(doc);
        DLDocumentActivity activity = new DLDocumentActivity(doc.getUpdatedBy(), DLActivityTypeEnum.RENAMED.getValue(),
                doc.getId(), doc.getId());
        dlDocumentActivityRepository.save(activity);
        return doc;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocument uploadDocuments(UploadDocumentDTO uploadDocumentDTO, MultipartFile[] files) throws Exception {
        log.info("DLDocumentService - uploadDocuments method called...");

        DLDocument dlDoc = null;
        if (files.length > 0) {
            for (MultipartFile file : files) {
                double allowedSize = 200 * 1024 * 1024;
                if (file.getSize() > allowedSize) {
                    throw new DataValidationException(AppUtility.getResourceMessage("invalid.file.size"));
                }
                if (StringUtils.containsAny(file.getOriginalFilename(), "/\\:*?\"<>|")) {
                    throw new DataValidationException(AppUtility.getResourceMessage("invalid.doc.name"));
                }
                log.info("DLDocumentService - buildDocument object started...");
                dlDoc = this.buildDocument(file, uploadDocumentDTO, true);
                log.info("DLDocumentService - buildDocument object ended...");

                String docLocation = dlDoc.getLocation();
                log.info("DLDocumentService - DocumentResponseWrapper locations is:" + docLocation);

                log.info("DLDocumentService - Creating temp file to upload....");
                File f = File.createTempFile(docLocation, dlDoc.getExtension());
                f.deleteOnExit();
                FileUtils.writeByteArrayToFile(f, file.getBytes());

                // UPLOADING ON FTP
                log.info("DLDocumentService - Uploaded on FTP started....");
                boolean isDocUploaded = ftpService.uploadFile(docLocation, dlDoc.getVersionGUId(), file.getInputStream());
                log.info("DLDocumentService - Uploaded on FTP ended with success: " + isDocUploaded);

                if (isDocUploaded) {
                    dlDoc = dlDocumentRepository.save(dlDoc);
                    DLDocumentActivity activity = new DLDocumentActivity(dlDoc.getCreatedBy(), DLActivityTypeEnum.UPLOADED.getValue(),
                            dlDoc.getId(), dlDoc.getId());
                    dlDocumentActivityRepository.save(activity);
                }
            }
        }
        return dlDoc;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocumentVersion createFirstDocumentVersion(DLDocument document, Long userId) {
        log.info("DLDocumentService - createFirstDocumentVersion method called...");

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
            doc.setName(file.getOriginalFilename());

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
            doc.setShared(null);
            doc.setFolder(false);
            if (DocumentUtil.isOCRType(doc)) {
                doc.setOcrSupported(true);
                doc.setOcrDone(false);
            } else {
                doc.setOcrSupported(false);
                doc.setOcrDone(true);
            }
            doc = dlDocumentRepository.save(doc);
            doc.setArchived(false);
            DLDocument folder = dlDocumentRepository.findByIdAndArchivedFalseAndFolderTrue(request.getFolderId());
            String selectedFolderPath = this.getNodePath(folder).toString();
            doc.setLocation(selectedFolderPath + "/");
            doc.setCurrentVersion(AppConstants.FIRST_VERSION);
            doc.setVersion(AppConstants.FIRST_VERSION);
            doc.setCreatedOn(ZonedDateTime.now());
            doc.setDocumentVersions(new ArrayList<>());
            DLDocumentVersion documentVersion = createFirstDocumentVersion(doc, request.getCreatedBy());
            documentVersion = dlDocumentVersionRepository.save(documentVersion);

            doc.getDocumentVersions().add(documentVersion);

            if (!isDocUpload) {
                switch (extension) {
                    case AppConstants.FileType.EXT_HTML:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_HTML);
                        doc.setExtension(AppConstants.FileType.EXT_HTML);
                        doc.setMimeType(AppConstants.MimeType.MIME_HTML);
                        break;
                    case AppConstants.FileType.EXT_DOC:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_DOC);
                        doc.setExtension(AppConstants.FileType.EXT_DOC);
                        doc.setMimeType(AppConstants.MimeType.MIME_DOC);
                        break;
                    case AppConstants.FileType.EXT_DOCX:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_DOCX);
                        doc.setExtension(AppConstants.FileType.EXT_DOCX);
                        doc.setMimeType(AppConstants.MimeType.MIME_DOCX);
                        break;
                    case AppConstants.FileType.EXT_XLS:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_XLS);
                        doc.setExtension(AppConstants.FileType.EXT_XLS);
                        doc.setMimeType(AppConstants.MimeType.MIME_XLS);
                        break;
                    case AppConstants.FileType.EXT_XLSX:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_XLSX);
                        doc.setExtension(AppConstants.FileType.EXT_XLSX);
                        doc.setMimeType(AppConstants.MimeType.MIME_XLSX);
                        break;
                    case AppConstants.FileType.EXT_PPT:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_PPT);
                        doc.setExtension(AppConstants.FileType.EXT_PPT);
                        doc.setMimeType(AppConstants.MimeType.MIME_PPT);
                        break;
                    case AppConstants.FileType.EXT_PPTX:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_PPTX);
                        doc.setExtension(AppConstants.FileType.EXT_PPTX);
                        doc.setMimeType(AppConstants.MimeType.MIME_PPTX);
                        break;
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
    @Transactional(rollbackFor = {Throwable.class})
    protected DLDocument buildFolderDocument(MultipartFile file, UploadFolderDTO request, boolean isDocUpload,String folderName) {
        DLDocument doc = new DLDocument();
        DLDocument dbDocs = dlDocumentRepository.findLatestDocumentByName(folderName);

        try {
            String title = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
            doc.setTitle(title);
            doc.setName(file.getOriginalFilename());
            int extDot = file.getOriginalFilename().lastIndexOf('.');
            String extension = extDot > 0 ? file.getOriginalFilename().substring(extDot + 1) : "";
            doc.setExtension(extension);
            doc.setMimeType(DocumentUtil.getMimeType(extension));
            doc.setSize(DocumentUtil.getFileSize(file.getSize()));
            doc.setSizeBytes(file.getSize());
            doc.setCreatedBy(request.getCreatedBy());
            doc.setUpdatedBy(request.getCreatedBy());
            doc.setCreatedOn(ZonedDateTime.now());
            doc.setLocation(folderName+"/");
            doc.setUpdatedOn(ZonedDateTime.now());
            doc.setParentId(dbDocs.getId());
            if (request.getFolderId() != 0) {
                doc.setParentId(request.getFolderId());
            }
            doc.setShared(null);
            doc.setFolder(false);
            if (DocumentUtil.isOCRType(doc)) {
                doc.setOcrSupported(true);
                doc.setOcrDone(false);
            } else {
                doc.setOcrSupported(false);
                doc.setOcrDone(true);
            }
            doc = dlDocumentRepository.save(doc);
            doc.setArchived(false);
            doc.setCurrentVersion(AppConstants.FIRST_VERSION);
            doc.setVersion(AppConstants.FIRST_VERSION);
            doc.setCreatedOn(ZonedDateTime.now());
            doc.setDocumentVersions(new ArrayList<>());
            DLDocumentVersion documentVersion = createFirstDocumentVersion(doc, request.getCreatedBy());
            documentVersion = dlDocumentVersionRepository.save(documentVersion);

            doc.getDocumentVersions().add(documentVersion);

            if (!isDocUpload) {
                switch (extension) {
                    case AppConstants.FileType.EXT_HTML:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_HTML);
                        doc.setExtension(AppConstants.FileType.EXT_HTML);
                        doc.setMimeType(AppConstants.MimeType.MIME_HTML);
                        break;
                    case AppConstants.FileType.EXT_DOC:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_DOC);
                        doc.setExtension(AppConstants.FileType.EXT_DOC);
                        doc.setMimeType(AppConstants.MimeType.MIME_DOC);
                        break;
                    case AppConstants.FileType.EXT_DOCX:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_DOCX);
                        doc.setExtension(AppConstants.FileType.EXT_DOCX);
                        doc.setMimeType(AppConstants.MimeType.MIME_DOCX);
                        break;
                    case AppConstants.FileType.EXT_XLS:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_XLS);
                        doc.setExtension(AppConstants.FileType.EXT_XLS);
                        doc.setMimeType(AppConstants.MimeType.MIME_XLS);
                        break;
                    case AppConstants.FileType.EXT_XLSX:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_XLSX);
                        doc.setExtension(AppConstants.FileType.EXT_XLSX);
                        doc.setMimeType(AppConstants.MimeType.MIME_XLSX);
                        break;
                    case AppConstants.FileType.EXT_PPT:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_PPT);
                        doc.setExtension(AppConstants.FileType.EXT_PPT);
                        doc.setMimeType(AppConstants.MimeType.MIME_PPT);
                        break;
                    case AppConstants.FileType.EXT_PPTX:
                        doc.setName(doc.getTitle().replaceAll(" ", "_") + "."
                                + AppConstants.FileType.EXT_PPTX);
                        doc.setExtension(AppConstants.FileType.EXT_PPTX);
                        doc.setMimeType(AppConstants.MimeType.MIME_PPTX);
                        break;
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
        folder.setOcrSupported(false);
        folder.setOcrDone(false);
        folder = dlDocumentRepository.save(folder);

        DLDocumentActivity activity = new DLDocumentActivity(folder.getCreatedBy(), DLActivityTypeEnum.CREATED.getValue(),
                folder.getId(), folder.getId());
        activity.setCreatedBy(folderRequestDTO.getCreatedBy());
        activity.setUpdatedBy(folderRequestDTO.getUpdatedBy());
        dlDocumentActivityRepository.save(activity);

        return folder;
    }

    public DLDocument archiveDlDocument(Long dlDocumentId) {
        log.info("archiveDlDocument method called..");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        DLDocument doc = null;
        if (opDoc.isPresent()) {
            doc = opDoc.get();
            doc.setDaysArchived(0);
            doc.setArchived(true);
            doc.setArchivedOn(ZonedDateTime.now());
            dlDocumentRepository.save(doc);
        }
        DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.ARCHIVED.getValue(),
                doc.getId(), doc.getId());
        dlDocumentActivityRepository.save(activity);
        return doc;
    }

    public DLDocument unArchiveDlDocument(Long dlDocumentId) {
        log.info("unArchiveDlDocument method called..");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        DLDocument doc = null;
        if (opDoc.isPresent()) {
            doc = opDoc.get();
            doc.setDaysArchived(0);
            doc.setArchived(Boolean.FALSE);
            doc.setArchivedOn(ZonedDateTime.now());
            dlDocumentRepository.save(doc);
        }
        DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.RESTORED_ARCHIVED.getValue(),
                doc.getId(), doc.getId());
        dlDocumentActivityRepository.save(activity);
        return doc;
    }

    @Transactional(rollbackFor = {Throwable.class})
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

    @Transactional(rollbackFor = {Throwable.class})
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

    public DLDocumentDTO getDocumentByGUID(String guid, Boolean shared) {
        log.info("DLDocumentService - getDocumentByGUID method called...");

        DLDocument document = null;
        if (shared) {
            document = dlDocumentRepository.findByVersionGUIdAndArchivedFalseAndFolderFalseAndSharedTrue(guid);
        } else {
            document = dlDocumentRepository.findByVersionGUIdAndArchivedFalseAndFolderFalse(guid);
        }
        if (!AppUtility.isEmpty(document)) {
            DLDocumentDTO dlDocumentDTO = new DLDocumentDTO();
            dlDocumentDTO.convertToDTO(document, false);

            return dlDocumentDTO;
        }
        return null;
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
                dlDocumentActivityRepository.save(activity);
            } else {

            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        return inputStreamResource;
    }

    public InputStreamResource transferPDFToDocument(Long dlDocumentId, boolean isText, boolean isPpt, boolean isExcel) throws Exception {
        log.info("DLDocumentService - transferPDFToDocument method called...");
        InputStreamResource inputStreamResource = null;
        FileOutputStream fos = null;
        XWPFDocument document = null;

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        if (opDoc.isPresent()) {
            DLDocument doc = opDoc.get();
            if (!doc.getFolder()) {
                File image = ftpService.downloadFile(doc.getVersionGUId());
                PDDocument doc1 = PDDocument.load(image);
                PDFRenderer pdfRenderer = new PDFRenderer(doc1);

                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
                // Create a temp image file
                File tempFile = File.createTempFile("tempfile_" + 0, ".png");
                ImageIO.write(bufferedImage, "png", tempFile);

                Tesseract tesseract = new Tesseract();
                tesseract.setDatapath(environment.getProperty("ocr.tessdata.path"));
                tesseract.setLanguage("eng");

                try {
                    String imageText = tesseract.doOCR(tempFile);
                    if(isText) {
                        Path file = Paths.get("file.txt");
                        Files.write(file, imageText.getBytes(StandardCharsets.UTF_8));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imageText.getBytes(StandardCharsets.UTF_8)));

                    } else if(isPpt) {

                        String outputFileName = doc.getTitle()+".pptx";

                        XMLSlideShow ppt = new XMLSlideShow();
                        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
                        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
                        XSLFSlide slide = ppt.createSlide(layout);

                        XSLFTextShape title = slide.getPlaceholder(0);
                        title.setText("Image Text");

                        XSLFTextShape content = slide.getPlaceholder(1);
                        content.clearText();
                        XSLFTextParagraph p = content.addNewTextParagraph();
                        p.setBullet(false);
                        XSLFTextRun r = p.addNewTextRun();
                        r.setText(imageText);
                        //r.setFontColor(Color.green);
                        //r.setFontSize(24.);

                        FileOutputStream out = new FileOutputStream(outputFileName);
                        ppt.write(out);
                        out.close();

                        File file = ResourceUtils.getFile(outputFileName);
                        byte[] contents = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(contents));

                    } else if(isExcel) {

                        String outputFileName = doc.getTitle()+".xlsx";

                        XSSFWorkbook workbook = new XSSFWorkbook();
                        XSSFSheet sheet = workbook.createSheet("Sheet1");
                        Row row = sheet.createRow(1);
                        Cell cell = row.createCell(1);
                        cell.setCellValue(imageText);


                        FileOutputStream out = new FileOutputStream(outputFileName);
                        workbook.write(out);
                        out.close();

                        File file = ResourceUtils.getFile(outputFileName);
                        byte[] contents = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(contents));

                    }else {

                        String outputFileName = doc.getTitle()+".doc";

                        document = new XWPFDocument();
                        XWPFParagraph paragraph = document.createParagraph();
                        paragraph.setFirstLineIndent(400);
                        // paragraph.setAlignment(ParagraphAlignment.DISTRIBUTE);
                        // paragraph.setWordWrapped(true);

                        XWPFRun run = paragraph.createRun();
                        run.setText(imageText);

                        fos = new FileOutputStream(outputFileName);
                        document.write(fos);

                        File file = ResourceUtils.getFile(outputFileName);
                        byte[] contents = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(contents));
                    }
                } catch (TesseractException ex) {
                    log.error(ex);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (document != null) {
                        try {
                            document.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.DOWNLOADED.getValue(),
                        doc.getId(), doc.getId());
                dlDocumentActivityRepository.save(activity);
            } else {
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }

        return inputStreamResource;
    }
    public InputStreamResource transferImageToDocument(Long dlDocumentId, boolean isText, boolean isPpt, boolean isExcel) throws Exception {

        log.info("DLDocumentService - transferImageToDocument method called...");
        InputStreamResource inputStreamResource = null;
        FileOutputStream fos = null;
        XWPFDocument document = null;

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);

        if (opDoc.isPresent()) {

            DLDocument doc = opDoc.get();

            if (!doc.getFolder()) {

                File image = ftpService.downloadFile(doc.getVersionGUId());
                Tesseract tesseract = new Tesseract();
                tesseract.setDatapath(environment.getProperty("ocr.tessdata.path"));
                tesseract.setLanguage("eng");
                //tesseract.setPageSegMode(1);
                //tesseract.setOcrEngineMode(1);

                try {

                    String imageText = tesseract.doOCR(image);

                    if(isText) {

                        Path file = Paths.get("file.txt");
                        Files.write(file, imageText.getBytes(StandardCharsets.UTF_8));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imageText.getBytes(StandardCharsets.UTF_8)));

                    } else if(isPpt) {

                        String outputFileName = doc.getTitle()+".pptx";

                        XMLSlideShow ppt = new XMLSlideShow();
                        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
                        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
                        XSLFSlide slide = ppt.createSlide(layout);

                        XSLFTextShape title = slide.getPlaceholder(0);
                        title.setText("Image Text");

                        XSLFTextShape content = slide.getPlaceholder(1);
                        content.clearText();
                        XSLFTextParagraph p = content.addNewTextParagraph();
                        p.setBullet(false);
                        XSLFTextRun r = p.addNewTextRun();
                        r.setText(imageText);
                        //r.setFontColor(Color.green);
                        //r.setFontSize(24.);

                        FileOutputStream out = new FileOutputStream(outputFileName);
                        ppt.write(out);
                        out.close();

                        File file = ResourceUtils.getFile(outputFileName);
                        byte[] contents = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(contents));

                    } else if(isExcel) {

                        String outputFileName = doc.getTitle()+".xlsx";

                        XSSFWorkbook workbook = new XSSFWorkbook();
                        XSSFSheet sheet = workbook.createSheet("Sheet1");
                        Row row = sheet.createRow(1);
                        Cell cell = row.createCell(1);
                        cell.setCellValue(imageText);


                        FileOutputStream out = new FileOutputStream(outputFileName);
                        workbook.write(out);
                        out.close();

                        File file = ResourceUtils.getFile(outputFileName);
                        byte[] contents = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(contents));

                    }else {

                        String outputFileName = doc.getTitle()+".doc";

                        document = new XWPFDocument();
                        XWPFParagraph paragraph = document.createParagraph();
                        paragraph.setFirstLineIndent(400);
                        // paragraph.setAlignment(ParagraphAlignment.DISTRIBUTE);
                        // paragraph.setWordWrapped(true);

                        XWPFRun run = paragraph.createRun();
                        run.setText(imageText);

                        fos = new FileOutputStream(outputFileName);
                        document.write(fos);

                        File file = ResourceUtils.getFile(outputFileName);
                        byte[] contents = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(contents));
                    }
                } catch (TesseractException ex) {
                    log.error(ex);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (document != null) {
                        try {
                            document.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.DOWNLOADED.getValue(),
                        doc.getId(), doc.getId());
                dlDocumentActivityRepository.save(activity);
            } else {
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }

        return inputStreamResource;
    }
    public InputStreamResource downloadDLFolder(Long dlDocumentId) throws Exception {
        log.info("DLDocumentService - downloadFolder method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        List<DLDocument> locationList = dlDocumentRepository.findAllByParentIdAndArchivedStatus(opDoc.get().getId(),false);
        List<String> dbFileNames = new ArrayList<>();
        File folderTozip = new File("C:\\Users\\admin\\Desktop\\" + opDoc.get().getName());
        String dir = "C:\\Users\\admin\\Desktop\\" + opDoc.get().getName() + ".zip";
        DLDocument doc;
        File nFolder = null;
        File newFile = null;
        InputStream inputStream = null;
        InputStreamResource inputStreamResource;
        FileInputStream fis = null;
        ZipOutputStream zos = null;
        FileOutputStream fos;
        if (opDoc.isPresent()) {
            doc = opDoc.get();
            if (doc.getFolder()) {
                for (DLDocument dbFiles : locationList) {
                    if (dbFiles.getFolder()){
                         nFolder = new File(folderTozip.toString()+"\\"+dbFiles.getName());
                        nFolder.mkdirs();
                        List<DLDocument> fileList = dlDocumentRepository.findAllByParentIdAndArchivedStatus(dbFiles.getId(),false);
                        for (DLDocument dbFile:fileList){
                            if (dbFile.getFolder()){
                                File sFolder = new File(nFolder.toString()+"\\"+dbFile.getName());
                                sFolder.mkdirs();
                                List<DLDocument> sFileList = dlDocumentRepository.findAllByParentIdAndArchivedStatus(dbFile.getId(),false);
                                for (DLDocument sDbFile:sFileList){
                                    inputStream = ftpService.downloadInputStream(sDbFile.getVersionGUId());
                                    newFile = new File(sFolder.toString() + File.separator + sDbFile.getName());
                                    FileUtils.copyInputStreamToFile(inputStream, newFile);
                                }
                                break;
                            }else {
                                dbFileNames.add(dbFile.getName());
                            }
                            inputStream = ftpService.downloadInputStream(dbFile.getVersionGUId());
                            newFile = new File(nFolder.toString() + File.separator + dbFile.getName());
                            FileUtils.copyInputStreamToFile(inputStream, newFile);
                        }
                        break;
                    } else {
                        dbFileNames.add(dbFiles.getName());
                    }
                    inputStream = ftpService.downloadInputStream(dbFiles.getVersionGUId());
                    newFile = new File(folderTozip.toString() + File.separator + dbFiles.getName());
                    FileUtils.copyInputStreamToFile(inputStream, newFile);
                    inputStreamResource = new InputStreamResource(inputStream);
                }
                File newDirectory = new File(folderTozip.toString());
                boolean isCreated = newDirectory.mkdirs();
                if (isCreated) {
                    log.info("Successfully created directories, path:%s",newDirectory.getCanonicalPath());
                } else if (newDirectory.exists()) {
                    log.info("Directory path already exist, path:%s",newDirectory.getCanonicalPath());
                } else {
                    log.error("Unable to create directory");
                }
                populateFilesList(folderTozip,dbFileNames);
                fos = new FileOutputStream(dir);
                zos = new ZipOutputStream(fos);
                for (String filePath : filesListInDir) {
                    log.info("Zipping " + filePath);

                    //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                    ZipEntry ze = new ZipEntry(filePath.substring(folderTozip.getAbsolutePath().length() + 1, filePath.length()));
                    zos.putNextEntry(ze);
                    //read the file and write to ZipOutputStream
                    fis = new FileInputStream(filePath);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                    fis.close();
                }
                filesListInDir.clear();

                zos.close();
                fos.close();

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.DOWNLOADED.getValue(),
                        doc.getId(), doc.getId());
                dlDocumentActivityRepository.save(activity);
            } else {
                throw new DataValidationException(AppUtility.getResourceMessage("folder.not.found"));
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("folder.not.found"));
        }
        File file = new File(dir);
        FileInputStream stream = new FileInputStream(file);
        inputStreamResource = new InputStreamResource(stream);

        return inputStreamResource;
    }
    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir, List<String> dbFileNames) throws IOException {
        File[] files = dir.listFiles();
        for(File fil : files){
            if(fil.isFile()) {
                if(dbFileNames.contains(fil.getName())) {
                    filesListInDir.add(fil.getAbsolutePath());
                }
            }
            else populateFilesList(fil,dbFileNames);
        }
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
        if (AppUtility.isEmpty(docRestoreDTO)) {
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
                doc.setDaysArchived(null);
                doc.setArchivedOn(null);
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
                    DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.RESTORED_ARCHIVED.getValue(),
                            doc.getId(), doc.getId());
                    dlDocumentActivityRepository.save(activity);
                } catch (Exception e) {
                    ResponseUtility.exceptionResponse(e);
                }
            }
        }
    }

    public Boolean isImage(DLDocument doc) {
        return (!doc.getFolder()) && (!AppUtility.isEmpty(doc.getExtension())) && ((doc.getExtension().contains("gif")) ||
                (doc.getExtension().contains("png")) || (doc.getExtension().contains("jpeg")) || (doc.getExtension().contains("jpg")));
    }

    public Boolean isDoc(DLDocument doc) {
        return (!doc.getFolder()) && (!AppUtility.isEmpty(doc.getExtension())) && ((doc.getExtension().contains("doc")) ||
                (doc.getExtension().contains("docx")) || (doc.getExtension().contains("html")) || (doc.getExtension().contains("odt")) ||
                (doc.getExtension().contains("xls")) || (doc.getExtension().contains("pdf")) || (doc.getExtension().contains("xlsx")) ||
                (doc.getExtension().contains("ods")) || (doc.getExtension().contains("pptx")) || (doc.getExtension().contains("ppt")) ||
                (doc.getExtension().contains("txt")));
    }

    public Boolean isVideo(DLDocument doc) {
        return (!doc.getFolder()) && (!AppUtility.isEmpty(doc.getExtension())) && ((doc.getExtension().contains("mp4")) ||
                (doc.getExtension().contains("mov")) || (doc.getExtension().contains("wmv")) || (doc.getExtension().contains("avi")) ||
                (doc.getExtension().contains("flv")) || (doc.getExtension().contains("mkv")) || (doc.getExtension().contains("webm")));
    }

    public Boolean isFolder(DLDocument doc) {
        return doc.getFolder();
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocument uploadFolder(UploadFolderDTO uploadFolderDTO, MultipartFile[] files,String folderName) throws Exception {
        log.info("DLDocumentService - uploadDocuments method called...");

        DLDocument dlDoc = null;
        if (files.length > 0) {
            for (MultipartFile file : files) {
                double allowedSize = 200 * 1024 * 1024;
                if (file.getSize() > allowedSize) {
                    throw new DataValidationException(AppUtility.getResourceMessage("invalid.file.size"));
                }
                if (StringUtils.containsAny(file.getOriginalFilename(), "/\\:*?\"<>|")) {
                    throw new DataValidationException(AppUtility.getResourceMessage("invalid.doc.name"));
                }
                log.info("DLDocumentService - buildDocument object started...");
                dlDoc = this.buildFolderDocument(file, uploadFolderDTO, true,folderName);
                log.info("DLDocumentService - buildDocument object ended...");

                String docLocation = dlDoc.getLocation();
                log.info("DLDocumentService - DocumentResponseWrapper locations is:" + docLocation);


                log.info("DLDocumentService - Creating temp file to upload....");
                File f = File.createTempFile(docLocation, dlDoc.getExtension());
                f.deleteOnExit();
                FileUtils.writeByteArrayToFile(f, file.getBytes());

                // UPLOADING ON FTP
                log.info("DLDocumentService - Uploaded on FTP started....");
                boolean isDocUploaded = ftpService.uploadFile(docLocation, dlDoc.getVersionGUId(), file.getInputStream());
                log.info("DLDocumentService - Uploaded on FTP ended with success: " + isDocUploaded);

                if (isDocUploaded) {
                    dlDoc = dlDocumentRepository.save(dlDoc);
                    DLDocumentActivity activity = new DLDocumentActivity(dlDoc.getCreatedBy(), DLActivityTypeEnum.UPLOADED.getValue(),
                            dlDoc.getId(), dlDoc.getId());
                    dlDocumentActivityRepository.save(activity);
                }
            }
        }
        return dlDoc;
    }
    public void uploadfolderName(String folderName,UploadFolderDTO uploadFolderDTO){
        DLDocument dlDocument = new DLDocument();
        dlDocument.setTitle(folderName);
        dlDocument.setName(folderName);
        dlDocument.setFolder(true);
        dlDocument.setCreatedBy(uploadFolderDTO.getCreatedBy());
        dlDocument.setUpdatedBy(uploadFolderDTO.getUpdatedBy());
        dlDocument.setCreatedOn(ZonedDateTime.now());
        dlDocument.setUpdatedOn(ZonedDateTime.now());
        dlDocument.setArchived(false);
        dlDocument.setOcrDone(false);
        dlDocument.setOcrSupported(false);
        dlDocument.setLeafNode(false);
        dlDocumentRepository.save(dlDocument);
    }
    public InputStreamResource viewDLDocument(Long dlDocumentId) throws Exception {
        log.info("DLDocumentService - viewDLDocument method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        File file;
        File viewFilePath;
        InputStreamResource inputStreamResource1 = null;

        if (opDoc.isPresent()) {
            DLDocument doc = opDoc.get();
            viewFilePath = new File("C:\\Users\\admin\\WebstormProjects\\docyard-fe\\src\\assets\\files");
            if (!doc.getFolder()) {
                InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                file = new File(viewFilePath.toString() + File.separator + doc.getName());
                FileUtils.copyInputStreamToFile(inputStream, file);
                inputStreamResource1 = new InputStreamResource(inputStream);

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.FILE_VIEWED.getValue(),
                        doc.getId(), doc.getId());
                dlDocumentActivityRepository.save(activity);
            } else {

            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        return inputStreamResource1;
    }

    public InputStreamResource lockDLDocument(Long dlDocumentId) throws Exception {
        log.info("DLDocumentService - lockDLDocument method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        File file;
        InputStreamResource inputStreamResource1 = null;
        FileLock lock = null;
        RandomAccessFile fileToLock;
        FileChannel channel;

        if (opDoc.isPresent()) {
            DLDocument doc = opDoc.get();
            if (!doc.getFolder()) {
                InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                file = new File(doc.getName());
                file.setReadOnly();
                fileToLock = new RandomAccessFile(inputStream.toString(), "rw");
                channel = fileToLock.getChannel();
                lock = channel.lock();
                if (lock.isValid()){
                    log.info("File locked Successfully!");
                }
           //     FileUtils.copyInputStreamToFile(inputStream, file);
                inputStreamResource1 = new InputStreamResource(inputStream);

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.FILE_VIEWED.getValue(),
                        doc.getId(), doc.getId());
                dlDocumentActivityRepository.save(activity);
            } else {
                throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        fileToLock.close();
        channel.close();
        return inputStreamResource1;
    }

    public InputStreamResource unLockDLDocument(Long dlDocumentId) throws Exception {
        log.info("DLDocumentService - unLockDLDocument method called...");

        Optional<DLDocument> opDoc = dlDocumentRepository.findById(dlDocumentId);
        File file;
        InputStreamResource inputStreamResource1 = null;
        FileLock lock = null;
        RandomAccessFile fileToUnLock;
        FileChannel channel;

        if (opDoc.isPresent()) {
            DLDocument doc = opDoc.get();
            if (!doc.getFolder()) {
                InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                file = new File(doc.getName());
                file.setWritable(true);
                fileToUnLock = new RandomAccessFile(inputStream.toString(), "rw");
                channel = fileToUnLock.getChannel();
                lock = channel.lock();
                if (lock.isValid()){
                    lock.release();
                }
                FileUtils.copyInputStreamToFile(inputStream, file);
                inputStreamResource1 = new InputStreamResource(inputStream);

                DLDocumentActivity activity = new DLDocumentActivity(doc.getCreatedBy(), DLActivityTypeEnum.FILE_VIEWED.getValue(),
                        doc.getId(), doc.getId());
                dlDocumentActivityRepository.save(activity);
            } else {
                throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        fileToUnLock.close();
        channel.close();

        return inputStreamResource1;
    }

    public List<DLDocumentDTO> getAllArchivedDLDocumentByDocId(Long parentId,boolean archived) {
        log.info("DLDocumentService - getAllArchivedDLDocumentByOwnerId method called...");

        List<DLDocumentDTO> documentDTOList = new ArrayList<>();
        List<DLDocument> archivedDlDocuments = dlDocumentRepository.findAllByParentIdAndArchivedStatus(parentId,archived);
        for (DLDocument doc : archivedDlDocuments) {
            DLDocumentDTO dto = new DLDocumentDTO();
            dto.convertToDTO(doc, false);

            if (doc.getFolder()) {
                int fileCount = dlDocumentRepository.countAllByArchivedFalseAndFolderFalseAndParentId(doc.getId());
                dto.setSize(fileCount + " Files");
            }
            Object response = restTemplate.getForObject("http://um-service/um/user/" + parentId, Object.class);
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
    public void checkInCheckOutDLDocument(Long documentId, Long userId, Boolean flag) {

        log.info("DLDocumentService - checkInCheckOutDLDocument method called...");
        String activityType;

        Optional<DLDocument> dlDocument = dlDocumentRepository.findById(documentId);

        if (dlDocument.isPresent()) {

            DLDocument document = dlDocument.get();

            if (document.getCheckedIn() != null && document.getCheckedIn().equals(flag)){
                throw new DataValidationException(AppUtility.getResourceMessage("The file has already been checked-in by another user."));
            } else {
                document.setCheckedIn(flag);
            }

            if (flag) {
                document.setCheckedInBy(userId);
                activityType = DLActivityTypeEnum.CHECKED_IN.getValue();
            } else {
                document.setCheckedInBy(null);
                activityType = DLActivityTypeEnum.CHECKED_OUT.getValue();
            }

            dlDocumentRepository.save(document);

            DLDocumentActivity activity = new DLDocumentActivity(document.getCreatedBy(), activityType,
                    document.getId(), document.getId());
            dlDocumentActivityRepository.save(activity);
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
    }
}