package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.*;
import com.infotech.docyard.dochandling.dl.repository.*;
import com.infotech.docyard.dochandling.dto.ShareRequestDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.enums.ShareStatusEnum;
import com.infotech.docyard.dochandling.enums.ShareTypeEnum;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public String shareDLDocument(ShareRequestDTO shareRequest) throws IOException {
        log.info("DLShareService - shareDLDocument method called...");

        String status = null;
        DLDocument dlDocument = dlDocumentRepository.findByIdAndArchivedFalse(shareRequest.getDlDocId());
        if (AppUtility.isEmpty(dlDocument)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));

        }
        if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.OFF_SPECIFIC.getValue())) {
            status = this.shareOffSpecific(shareRequest, dlDocument);
        } else if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.INVITED_EXTERNAL_PEOPLE_ONLY.getValue())) {
            //status = this.shareInvitedExternalPeopleOnly(tenantId, shareRequest, user, deviceType, document, folder, accountSettings);
        } else if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.INVITED_PEOPLE_ONLY.getValue())) {
//            status = this.shareInvitedInternalMemberOnly(tenantId, shareRequest, user, deviceType, document, folder, accountSettings);
            status = shareInvitedInternalMemberOnly(shareRequest, dlDocument);
        } else if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.NO_SHARING.getValue())) {
            //status = this.terminateShare(tenantId, shareRequest, user, deviceType, document, folder, accountSettings);
        }
        return status;
    }

    private String shareOffSpecific(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - shareOffSpecific method called...");

        DLShare dlShare = new DLShare();
        String status = "NOT_FOUND";
        if (dlDocument.getShared()) {
            Optional<DLShare> dsOp = dlShareRepository.findById(dlDocument.getDlShareId());
            if (dsOp.isPresent()) {
                dlShare = dsOp.get();
            }
        }
        dlShare.setDlDocumentId(dlDocument.getId());
        dlDocument.setShared(true);
        dlDocument.setShareType(ShareTypeEnum.OFF_SPECIFIC.getValue());
        int shareLinkCount = dlDocument.getShareLinkCount();
        shareLinkCount++;
        dlDocument.setShareLinkCount(shareLinkCount);
        dlDocumentRepository.save(dlDocument);

        if (dlShare.getShareType().equalsIgnoreCase(ShareTypeEnum.INVITED_PEOPLE_ONLY.getValue())) {
            dlShareCollaboratorRepository.deleteByDlShareId(dlShare.getId());
        }
        dlShare.setPermanentLink(shareRequest.getShareLink());
        dlShare.setAccessRight(shareRequest.getSharePermission());
        dlShare.setShareType(ShareTypeEnum.OFF_SPECIFIC.getValue());
        dlShare.setStatus(ShareStatusEnum.SHARED.getValue());
        dlShare.setShareNotes(shareRequest.getMessage());

        dlShareRepository.save(dlShare);

        DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.OPEN_LINK.getValue(),
                dlShare.getId(), dlDocument.getId());
        activity.setCreatedOn(ZonedDateTime.now());

        dlDocumentActivityRepository.save(activity);

        // send FCM to specific user
        status = "SUCCESS";
        return status;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String shareInvitedInternalMemberOnly(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - shareInvitedInternalMemberOnly method called...");

        DLShare dlShare = new DLShare();
        String status = "NOT_FOUND";
        if (dlDocument.getShared()) {
            Optional<DLShare> dsOp = dlShareRepository.findById(dlDocument.getDlShareId());
            if (dsOp.isPresent()) {
                dlShare = dsOp.get();
            }
        }
        dlShare.setDlDocumentId(dlDocument.getId());
        dlDocument.setShared(true);
        dlDocument.setShareType(ShareTypeEnum.INVITED_PEOPLE_ONLY.getValue());
        int shareLinkCount = dlDocument.getShareLinkCount();
        shareLinkCount++;
        dlDocument.setShareLinkCount(shareLinkCount);
        dlDocumentRepository.save(dlDocument);
        dlShare.setPermanentLink(shareRequest.getShareLink());
        dlShare.setAccessRight(shareRequest.getSharePermission());
        dlShare.setShareType(ShareTypeEnum.INVITED_PEOPLE_ONLY.getValue());
        dlShare.setStatus(ShareStatusEnum.SHARED.getValue());
        dlShare.setShareNotes(shareRequest.getMessage());
        dlShare.setCreatedOn(ZonedDateTime.now());
        dlShare.setUpdatedOn(ZonedDateTime.now());

        dlShare = dlShareRepository.save(dlShare);

        List<DLCollaborator> dlCollList = new ArrayList<>();
        for (String collaboratorEmail : shareRequest.getDlCollaborators()) {
            DLCollaborator dlCollaborator = new DLCollaborator();
            dlCollaborator.setEmail(collaboratorEmail);
            dlCollaborator.setCreatedOn(ZonedDateTime.now());
            dlCollaborator.setUpdatedOn(ZonedDateTime.now());
            dlCollaborator.setDlShareCollaborators(new ArrayList<>());

            dlCollList.add(dlCollaborator);
        }
        dlCollList = dlCollaboratorRepository.saveAll(dlCollList);

        List<DLShareCollaborator> scList = new ArrayList<>();
        for (DLCollaborator col : dlCollList) {
            DLShareCollaborator sc = new DLShareCollaborator();
            sc.setDlShareId(dlShare.getId());
            sc.setAccessRight(shareRequest.getSharePermission());
            sc.setDlCollaboratorId(col.getId());
            sc.setCreatedOn(ZonedDateTime.now());
            sc.setUpdatedOn(ZonedDateTime.now());

            scList.add(sc);
        }
        dlShareCollaboratorRepository.saveAll(scList);

        dlShare.setDlShareCollaborators(scList);
        dlShare = dlShareRepository.save(dlShare);

        DLDocumentActivity activity = new DLDocumentActivity(dlDocument.getCreatedBy(), DLActivityTypeEnum.OPEN_LINK.getValue(),
                dlShare.getId(), dlDocument.getId());
        activity.setCreatedOn(ZonedDateTime.now());
        dlDocumentActivityRepository.save(activity);
        // send FCM to specific user
        status = "SUCCESS";
        return status;
    }
}
