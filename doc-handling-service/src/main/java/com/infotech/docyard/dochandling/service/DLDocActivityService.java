package com.infotech.docyard.dochandling.service;


import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentActivityRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentCommentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.dto.DLDocumentActivityDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentActivityResponseDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeMessageEnum;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DateTimeUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Log4j2
public class DLDocActivityService {

    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DLDocumentCommentRepository dlDocumentCommentRepository;
    @Autowired
    private DLDocumentRepository dlDocumentRepository;


    public List<DLDocumentActivityResponseDTO> getAllActivitiesByUserId(Long userId) {
        log.info("DLDocActivityService - getAllActivitiesByUserId method called...");

        List<DLDocumentActivityDTO> dlDocumentActivityDTOList = new ArrayList<>();
        List<DLDocumentActivity> dlDocumentActivityList = dlDocumentActivityRepository.findAllByUserIdOrderByUpdatedOnDesc(userId);
        List<DLDocumentActivityResponseDTO> responseDTOList = new ArrayList<>();
        for (DLDocumentActivity docAct : dlDocumentActivityList) {
            DLDocumentActivityDTO dto = new DLDocumentActivityDTO();
            dto.convertToDTO(docAct, false);
            dlDocumentActivityDTOList.add(dto);
        }
        for (DLDocumentActivityDTO activityDTO : dlDocumentActivityDTOList) {
            DLDocumentActivityResponseDTO activityResponseDTO = new DLDocumentActivityResponseDTO();

            switch (activityDTO.getActivityType()) {
                case "COMMENT_POSTED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.COMMENT_POSTED.getValue());
                    Optional<DLDocumentComment> commentOP = dlDocumentCommentRepository.findById(activityDTO.getEntityId());
                    commentOP.ifPresent(comm -> activityResponseDTO.setComment(comm.getMessage()));
                    break;
                case "COMMENT_DELETED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.COMMENT_DELETED.getValue());
                    break;
                case "FILE_VIEWED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.FILE_VIEWED.getValue());
                    break;
                case "UPLOADED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.UPLOADED.getValue());
                    break;
                case "DOWNLOADED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.DOWNLOADED.getValue());
                    break;
                case "CREATED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.CREATED.getValue());
                    break;
                case "RENAMED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.RENAMED.getValue());
                    break;
                case "ARCHIVED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.ARCHIVED.getValue());
                    break;
                case "RESTORED_ARCHIVED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.RESTORED_ARCHIVED.getValue());
                    break;
                case "STARRED":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.STARRED.getValue());
                    break;
                case "SHARED_WITH_OPEN_LINK":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.SHARED_WITH_OPEN_LINK.getValue());
                    break;
                case "RESTRICTED_SHARE":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.RESTRICTED_SHARE.getValue());
                    break;
                case "NO_SHARING":
                    activityResponseDTO.setAction(DLActivityTypeMessageEnum.NO_SHARING.getValue());
                    break;
                default:
                    break;
            }
            Object response = restTemplate.getForObject("http://um-service/um/user/" + activityDTO.getUserId(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                activityResponseDTO.setUserName((String) map.get("name"));
            }
            Optional<DLDocument> opDoc = dlDocumentRepository.findById(activityDTO.getDocId());
            opDoc.ifPresent(dlDocument -> activityResponseDTO.setDocName(dlDocument.getName()));
            activityResponseDTO.setActivityPerformedOn(DateTimeUtil.convertDateToUFDateFormat(activityDTO.getUpdatedOn()));
            responseDTOList.add(activityResponseDTO);
        }
        return responseDTOList;
    }
}
