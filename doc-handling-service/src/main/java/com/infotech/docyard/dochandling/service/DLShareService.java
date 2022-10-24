package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.*;
import com.infotech.docyard.dochandling.dl.repository.*;
import com.infotech.docyard.dochandling.dto.DLDocumentShareDTO;
import com.infotech.docyard.dochandling.dto.DLShareDTO;
import com.infotech.docyard.dochandling.dto.ShareRequestDTO;
import com.infotech.docyard.dochandling.enums.AccessRightEnum;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.enums.ShareStatusEnum;
import com.infotech.docyard.dochandling.enums.ShareTypeEnum;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppUtility;
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
                    dto.setDlCollId(dsc.getDlCollaboratorId());
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
        } else if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.NO_SHARING.getValue())) {
            status = removeSharing(shareRequest, dlDocument);
        }
        return status;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String shareAnyone(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - shareAnyone method called...");

        DLShare dlShare = new DLShare();
        String status = "NOT_FOUND";
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

        // send FCM to specific user
        status = "SUCCESS";
        return status;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String removeSharing(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - removeSharing method called...");

        String status = "NOT_SHARING";
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
        log.info("DLShareService - shareInvitedInternalMemberOnly method called...");

        DLShare dlShare = new DLShare();
        String status = "NOT_FOUND";
        List<String> emails = new ArrayList<>();
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

        if (!AppUtility.isEmpty(shareRequest.getDepartmentId())) {
            Object response = restTemplate.getForObject("http://um-service/um/user/department/" + shareRequest.getDepartmentId(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                /*emails = (ArrayList<String>) ((LinkedHashMap<?, ?>) response).get("item");*/
                emails = (ArrayList<String>) response;
            }
        }
        if (!AppUtility.isEmpty(shareRequest.getDlCollaborators())) {
            Collections.addAll(emails, shareRequest.getDlCollaborators());
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
        // send FCM to specific user
        status = "SUCCESS";
        return status;
    }
}
