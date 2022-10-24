package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.*;
import com.infotech.docyard.dochandling.dl.repository.*;
import com.infotech.docyard.dochandling.dto.DLDocumentShareDTO;
import com.infotech.docyard.dochandling.dto.NameEmailDTO;
import com.infotech.docyard.dochandling.dto.DLShareDTO;
import com.infotech.docyard.dochandling.dto.ShareRequestDTO;
import com.infotech.docyard.dochandling.dto.UserDTO;
import com.infotech.docyard.dochandling.enums.*;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppConstants;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.NotificationUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Log4j2
public class DLShareService {

    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private DLShareCollaboratorRepository dlShareCollaboratorRepository;
    @Autowired
    private DLCollaboratorRepository dlCollaboratorRepository;
    @Autowired
    private DLShareRepository dlShareRepository;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailInstanceRepository emailInstanceRepository;
    @Autowired
    private RestTemplate restTemplate;

    public List<DLDocumentShareDTO> getAllSharingDetailsByDLDocId(Long dlDocId) {
        log.info("DLShareService - getAllSharingDetailsByDLDocId method called...");

        DLDocument dlDocument = dlDocumentRepository.findByIdAndArchivedFalse(dlDocId);
        if (AppUtility.isEmpty(dlDocument)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        List<DLDocumentShareDTO> dlDocumentShareDTOList = new ArrayList<>();
        DLShare dlShare = dlShareRepository.findByDlDocumentId(dlDocId);
        if (AppUtility.isEmpty(dlShare)) {
            throw new DataValidationException(AppUtility.getResourceMessage("share.not.found"));
        }
        Object response = restTemplate.getForObject("http://um-service/um/user/" + dlDocument.getCreatedBy(), Object.class);
        if (!AppUtility.isEmpty(response)) {
            HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
            DLDocumentShareDTO owner = new DLDocumentShareDTO();
            owner.setAccessRight(AccessRightEnum.OWNER.getValue());
            owner.setDlCollName((String) map.get("name"));
            owner.setDlCollEmail((String) map.get("email"));

            if (!AppUtility.isEmpty(map.get("profilePhoto"))) {
                owner.setDlCollPic(map.get("profilePhoto").toString());
            }
            dlDocumentShareDTOList.add(owner);
        }
        for (DLShareCollaborator dsc : dlShare.getDlShareCollaborators()) {
            Optional<DLCollaborator> dc = dlCollaboratorRepository.findById(dsc.getDlCollaboratorId());
            if (dc.isPresent()) {
                DLDocumentShareDTO dto = new DLDocumentShareDTO(dsc);
                dto.setDlCollEmail(dc.get().getEmail());
                Object coll = restTemplate.getForObject("http://um-service/um/user/email/" + dc.get().getEmail(), Object.class);
                if (!AppUtility.isEmpty(coll)) {
                    HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) coll).get("data");
                    dto.setDlCollName((String) map.get("name"));
                    dto.setDlCollEmail((String) map.get("email"));
                    if (!AppUtility.isEmpty(map.get("profilePhoto"))) {
                        dto.setDlCollPic(map.get("profilePhoto").toString());
                    }
                }
                dlDocumentShareDTOList.add(dto);
            }
        }
        return dlDocumentShareDTOList;
    }

    public DLShare getDLShareById(Long dlShareId) {
        log.info("DLShareService - getAllSharingDetailsByDLDocId method called...");

        Optional<DLShare> dlShareOptional = dlShareRepository.findById(dlShareId);
        return dlShareOptional.orElse(null);
    }

    @Transactional(rollbackFor = Throwable.class)
    public String shareDLDocument(ShareRequestDTO shareRequest) {
        log.info("DLShareService - shareDLDocument method called...");

        String status = null;
        DLDocument dlDocument = dlDocumentRepository.findByIdAndArchivedFalse(shareRequest.getDlDocId());
        if (AppUtility.isEmpty(dlDocument)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));

        }
        if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.ANYONE.getValue())) {
            status = this.shareAnyone(shareRequest, dlDocument);
        } else if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.RESTRICTED.getValue())) {
            status = shareRestricted(shareRequest, dlDocument);
        }
        return status;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String shareAnyone(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - shareAnyone method called...");

        DLShare dlShare = new DLShare();
        String status = "NOT_FOUND";
        List<String> emails = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (!AppUtility.isEmpty(dlDocument.getShared()) && dlDocument.getShared()) {
            Optional<DLShare> dsOp = dlShareRepository.findById(dlDocument.getDlShareId());
            if (dsOp.isPresent()) {
                dlShare = dsOp.get();
            }
        }
        dlShare.setDlDocumentId(dlDocument.getId());
        dlDocument.setShared(true);
        dlDocument.setShareType(ShareTypeEnum.ANYONE.getValue());
        Integer shareLinkCount = dlDocument.getShareLinkCount();
        if (AppUtility.isEmpty(shareLinkCount)) {
            shareLinkCount = 0;
        } else {
            shareLinkCount++;
        }
        dlDocument.setShareLinkCount(shareLinkCount);
        dlDocumentRepository.save(dlDocument);

        if (dlShare.getShareType().equalsIgnoreCase(ShareTypeEnum.RESTRICTED.getValue())) {
            dlShareCollaboratorRepository.deleteByDlShareId(dlShare.getId());
        }
        dlShare.setPermanentLink(shareRequest.getShareLink());
        dlShare.setAccessRight(shareRequest.getSharePermission());
        dlShare.setShareType(ShareTypeEnum.ANYONE.getValue());
        dlShare.setStatus(ShareStatusEnum.SHARED.getValue());
        dlShare.setShareNotes(shareRequest.getMessage());
        dlShare.setCreatedOn(ZonedDateTime.now());
        dlShare.setUpdatedOn(ZonedDateTime.now());

        dlShare = dlShareRepository.save(dlShare);

        DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.ANYONE.getValue(), dlShare.getId(), dlDocument.getId());
        dlDocumentActivityRepository.save(activity);

        dlDocument.setDlShareId(dlShare.getId());
        dlDocumentRepository.save(dlDocument);

        NameEmailDTO nmEmDTO = new NameEmailDTO();
        status = "SUCCESS";
        if (!AppUtility.isEmpty(shareRequest.getDepartmentId()) || !AppUtility.isEmpty(shareRequest.getDlCollaborators())) {
            nmEmDTO = getNamesAndEmails(shareRequest.getDepartmentId(), shareRequest.getDlCollaborators(), nmEmDTO);
            status = sendMail(shareRequest, dlDocument.getName(), nmEmDTO.getNames(), nmEmDTO.getEmails());
        }

        // send FCM to specific user
        return status;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String removeSharing(ShareRequestDTO shareRequest) {
        log.info("DLShareService - removeSharing method called...");

        DLDocument dlDocument = dlDocumentRepository.findByIdAndArchivedFalse(shareRequest.getDlDocId());
        if (AppUtility.isEmpty(dlDocument)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));

        }
        String status = "SHARING";
        if (!AppUtility.isEmpty(dlDocument.getShared()) && dlDocument.getShared()) {
            dlShareCollaboratorRepository.deleteByDlShareId(dlDocument.getDlShareId());
            dlShareRepository.deleteById(dlDocument.getDlShareId());
            dlDocument.setDlShareId(null);
            dlDocument.setShareType(null);
            dlDocument.setShared(null);
            dlDocument.setUpdatedOn(ZonedDateTime.now());
            dlDocumentRepository.save(dlDocument);

            DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.SHARING_REMOVED.getValue(), null, dlDocument.getId());
            dlDocumentActivityRepository.save(activity);
            status = "SHARING_REMOVED";
        }
        return status;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String shareRestricted(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - shareRestricted method called...");

        NameEmailDTO nameEmailDTO = null;
        DLShare dlShare = new DLShare();
        String status = "NOT_FOUND";
        List<String> emails = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (!AppUtility.isEmpty(dlDocument.getShared()) && dlDocument.getShared()) {
            Optional<DLShare> dsOp = dlShareRepository.findById(dlDocument.getDlShareId());
            if (dsOp.isPresent()) {
                dlShare = dsOp.get();
            }
        }
        dlShare.setDlDocumentId(dlDocument.getId());
        dlDocument.setShared(true);
        dlDocument.setShareType(ShareTypeEnum.RESTRICTED.getValue());
        Integer shareLinkCount = dlDocument.getShareLinkCount();
        if (AppUtility.isEmpty(shareLinkCount)) {
            shareLinkCount = 0;
        } else {
            shareLinkCount++;
        }
        dlDocument.setShareLinkCount(shareLinkCount);
        dlDocument.setUpdatedBy(shareRequest.getUserId());
        dlShare.setPermanentLink(shareRequest.getShareLink());
        dlShare.setAccessRight(shareRequest.getSharePermission());
        dlShare.setShareType(ShareTypeEnum.RESTRICTED.getValue());
        dlShare.setStatus(ShareStatusEnum.SHARED.getValue());
        dlShare.setShareNotes(shareRequest.getMessage());
        dlShare.setCreatedOn(ZonedDateTime.now());
        dlShare.setUpdatedOn(ZonedDateTime.now());
        dlShare.setUpdatedBy(shareRequest.getUserId());

        dlShare = dlShareRepository.save(dlShare);
        dlDocument.setDlShareId(dlShare.getId());
        dlDocumentRepository.save(dlDocument);

        NameEmailDTO nmEmDTO = new NameEmailDTO();
        status = "SUCCESS";
        if (!AppUtility.isEmpty(shareRequest.getDepartmentId()) || !AppUtility.isEmpty(shareRequest.getDlCollaborators())) {
            nmEmDTO = getNamesAndEmails(shareRequest.getDepartmentId(), shareRequest.getDlCollaborators(), nmEmDTO);
            emails = nmEmDTO.getEmails();
            names = nmEmDTO.getNames();
        }


        List<DLCollaborator> dlCollList = new ArrayList<>();
        for (String colEmail : emails) {
            DLCollaborator dc = dlCollaboratorRepository.findByEmail(colEmail);
            if (AppUtility.isEmpty(dc)) {
                DLCollaborator dlCollaborator = new DLCollaborator();
                dlCollaborator.setEmail(colEmail);
                dlCollaborator.setCreatedOn(ZonedDateTime.now());
                dlCollaborator.setUpdatedOn(ZonedDateTime.now());
                dlCollaborator.setCreatedBy(shareRequest.getUserId());
                dlCollaborator.setUpdatedBy(shareRequest.getUserId());
                dlCollaborator.setDlShareCollaborators(new ArrayList<>());
                dlCollaborator = dlCollaboratorRepository.save(dlCollaborator);
                dlCollList.add(dlCollaborator);
            } else {
                dlCollList.add(dc);
            }
        }
        List<DLShareCollaborator> scList = new ArrayList<>();
        for (DLCollaborator col : dlCollList) {
            DLShareCollaborator sc = new DLShareCollaborator();
            sc.setDlShareId(dlShare.getId());
            sc.setAccessRight(shareRequest.getSharePermission());
            sc.setDlCollaboratorId(col.getId());
            sc.setCreatedOn(ZonedDateTime.now());
            sc.setUpdatedOn(ZonedDateTime.now());
            sc.setCreatedBy(shareRequest.getUserId());
            sc.setUpdatedBy(shareRequest.getUserId());
            scList.add(sc);
        }
        dlShareCollaboratorRepository.saveAll(scList);

        DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.RESTRICTED.getValue(), dlShare.getId(), dlDocument.getId());
        dlDocumentActivityRepository.save(activity);

        dlDocument.setDlShareId(dlShare.getId());
        dlDocumentRepository.save(dlDocument);
        status = "SUCCESS";

        status = sendMail(shareRequest, dlDocument.getName(), nmEmDTO.getNames(), nmEmDTO.getEmails());
        // send FCM to specific user
        return status;
    }

    public String removeCollaboratorFromSharing (Long dlDocId, Long collId) {
        log.info("DLShareService - shareRestricted method called...");

        DLShare dlShare = dlShareRepository.findByDlDocumentId(dlDocId);
        if (!AppUtility.isEmpty(dlShare)) {
            List<DLShareCollaborator> shareColls = dlShareCollaboratorRepository.findAllByDlShareId(dlShare.getId());
            if (!AppUtility.isEmpty(shareColls)) {
                if (shareColls.size() == 1) {
                    ShareRequestDTO shareRequestDTO = new ShareRequestDTO();
                    shareRequestDTO.setDlDocId(dlDocId);
                    removeSharing(shareRequestDTO);
                } else {
                    dlShareCollaboratorRepository.deleteByDlShareIdAndDlCollaboratorId(dlShare.getId(), collId);
                }
            }
            return "SUCCESS";
        }
        return "UNSUCCESSFULL";
    }

    public NameEmailDTO getNamesAndEmails(Long dptId, String[] collabEmails, NameEmailDTO nameEmailDTO) {
        List<String> emails = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (!AppUtility.isEmpty(dptId)) {
            Object response1 = restTemplate.getForObject("http://um-service/um/user/details/department/" + dptId, Object.class);
            if (!AppUtility.isEmpty(response1)) {
                HashMap<?, ?> emailsMap = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response1).get("emails");
                emails = (List<String>) emailsMap.get("emails");
                HashMap<?, ?> namesMap = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response1).get("names");
                names = (List<String>) namesMap.get("names");
            }
        }
        if (!AppUtility.isEmpty(collabEmails)) {
            for (String email : collabEmails) {
                Object response2 = restTemplate.getForObject("http://um-service/um/user/email/" + email, Object.class);
                if (!AppUtility.isEmpty(response2)) {
                    HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response2).get("data");
                    String name = ((String) map.get("name"));
                    if (!names.contains(name) || !email.contains(email)) {
                        names.add(name);
                        emails.add(email);
                    }
                }
            }
        }
        nameEmailDTO.setNames(names);
        nameEmailDTO.setEmails(emails);
        return nameEmailDTO;
    }

    public String sendMail(ShareRequestDTO shareRequest, String docName, List<String> names, List<String> emails) {
        try {
            UserDTO ownerDTO = new UserDTO();
            Boolean emailed = false;
            Object response = restTemplate.getForObject("http://um-service/um/user/" + shareRequest.getUserId(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                ownerDTO.setName((String) map.get("name"));
                ownerDTO.setEmail((String) map.get("email"));
                ownerDTO.setUsername((String) map.get("username"));
            }
            for (int i = 0; i <= names.size() - 1; i++) {
                String content = NotificationUtility.buildRestrictedShareFileEmailContent(ownerDTO, names.get(i), docName, shareRequest.getAppContextPath() +
                        shareRequest.getShareLink());
                emailed = false;
                if (!AppUtility.isEmpty(content)) {
                    EmailInstance emailInstance = new EmailInstance();
                    emailInstance.setToEmail(emails.get(i));
                    emailInstance.setType(EmailTypeEnum.SHARE_FILE_RESTRICTED.getValue());
                    emailInstance.setSubject(AppConstants.EmailSubjectConstants.SHARE_FILE_RESTRICTED);
                    emailInstance.setContent(content);
                    emailInstance.setStatus(EmailStatusEnum.NOT_SENT.getValue());
                    emailInstance.setCreatedOn(ZonedDateTime.now());
                    emailInstance.setUpdatedOn(ZonedDateTime.now());
                    emailInstance.setCreatedBy(1L);
                    emailInstance.setUpdatedBy(1L);
                    emailInstanceRepository.save(emailInstance);
                    notificationService.sendEmail(emailInstance);
                    emailed = true;
                }
            }
            if (emailed){
                return "SUCCESS";
            }
            return "UNSUCCESSFUL";
        } catch (Exception e) {
            log.info(e);
            return "UNSUCCESSFUL";
        }
    }
}