package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentActivityRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentCommentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.dto.DLDocumentCommentDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Log4j2
public class DLDocCommentService {

    @Autowired
    private DLDocumentCommentRepository dlDocumentCommentRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;

    public List<DLDocumentCommentDTO> getAllCommentsByDocumentId(Long documentId) {
        log.info("DLDocCommentService - getAllCommentsByDocumentId method called...");

        List<DLDocumentCommentDTO> dlDocumentCommentDTOList = new ArrayList<>();
        List<DLDocumentComment> dlDocumentCommentList = dlDocumentCommentRepository.findAllByDlDocument_IdOrderByUpdatedOnDesc(documentId);
        for (DLDocumentComment docComm : dlDocumentCommentList) {
            DLDocumentCommentDTO dto = new DLDocumentCommentDTO();
            dto.convertToDTO(docComm, false);

            Object response = restTemplate.getForObject("http://um-service/um/user/" + docComm.getUpdatedBy(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                dto.setNameOfUser((String) map.get("name"));
            }
            dlDocumentCommentDTOList.add(dto);
        }
        return dlDocumentCommentDTOList;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public DLDocumentComment postAndUpdateDocumentComment(DLDocumentCommentDTO commentDTO) {
        log.info("DLDocCommentService - postAndUpdateDocumentComment method called...");

        DLDocumentComment dlDocumentComment;
        DLDocument dlDocument = dlDocumentRepository.findByIdAndArchivedFalseAndFolderFalse(commentDTO.getDocId());
        if (AppUtility.isEmpty(dlDocument)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        } else {
            dlDocumentComment = commentDTO.convertToEntity();
            dlDocumentCommentRepository.save(dlDocumentComment);

            DLDocumentActivity activity = new DLDocumentActivity(dlDocumentComment.getCreatedBy(), DLActivityTypeEnum.COMMENT_POSTED.getValue(),
                    dlDocumentComment.getId(), dlDocumentComment.getDlDocument().getId());
            dlDocumentActivityRepository.save(activity);
        }
        return dlDocumentComment;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void deleteDocumentComment(Long documentId) {
        log.info("DLDocCommentService - deleteDocumentComment method called...");

        Optional<DLDocumentComment> dlDocumentComment = dlDocumentCommentRepository.findById(documentId);
        if (dlDocumentComment.isPresent()) {
            dlDocumentCommentRepository.delete(dlDocumentComment.get());

            DLDocumentActivity activity = new DLDocumentActivity(dlDocumentComment.get().getCreatedBy(), DLActivityTypeEnum.COMMENT_DELETED.getValue(),
                    dlDocumentComment.get().getId(), dlDocumentComment.get().getId());
            dlDocumentActivityRepository.save(activity);
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
    }
}
