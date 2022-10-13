package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.dl.entity.DLShare;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentActivityRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLShareCollaboratorRepository;
import com.infotech.docyard.dochandling.dl.repository.DLShareRepository;
import com.infotech.docyard.dochandling.dto.ShareRequestDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.enums.ShareStatusEnum;
import com.infotech.docyard.dochandling.enums.ShareTypeEnum;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;

@Service
@Log4j2
public class DLShareService {

    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private DLShareCollaboratorRepository dlShareCollaboratorRepository;
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
            //status = this.shareInvitedInternalMemberOnly(tenantId, shareRequest, user, deviceType, document, folder, accountSettings);
        } else if (shareRequest.getShareType().equalsIgnoreCase(ShareTypeEnum.NO_SHARING.getValue())) {
            //status = this.terminateShare(tenantId, shareRequest, user, deviceType, document, folder, accountSettings);
        }
        return status;
    }

    private String shareOffSpecific(ShareRequestDTO shareRequest, DLDocument dlDocument) {
        log.info("DLShareService - shareOffSpecific method called...");

        DLShare dlShare;
        String status = "NOT_FOUND";
        if (dlDocument.getShared()) {
            dlShare = dlDocument.getDlShare();
        } else {
            dlShare = new DLShare();
        }
        dlShare.setDlDocument(dlDocument);
        dlDocument.setShared(true);
        dlDocument.setShareType(ShareTypeEnum.OFF_SPECIFIC.getValue());
        int shareLinkCount = dlDocument.getShareLinkCount();
        shareLinkCount++;
        dlDocument.setShareLinkCount(shareLinkCount);
        dlDocumentRepository.save(dlDocument);

        if (dlShare.getShareType().equalsIgnoreCase(ShareTypeEnum.INVITED_PEOPLE_ONLY.getValue())) {
            dlShareCollaboratorRepository.deleteByDlShare(dlShare);
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
}
