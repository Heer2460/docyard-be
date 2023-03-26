package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentTag;
import com.infotech.docyard.dochandling.dl.repository.AdvTagSearchRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentActivityRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentTagRepository;
import com.infotech.docyard.dochandling.dto.DLDocumentTagDTO;
import com.infotech.docyard.dochandling.enums.DLActivityTypeEnum;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.um.dl.entity.Group;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Log4j2
public class DLDocTagService {

    @Autowired
    private DLDocumentTagRepository dlDocumentTagRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private DLDocumentActivityRepository dlDocumentActivityRepository;

    @Autowired
    private AdvTagSearchRepository advTagSearchRepository;


    @Transactional(rollbackFor = {Throwable.class})
    public DLDocumentTag postAndUpdateDocumentTag(DLDocumentTagDTO tagDTODTO) {
        log.info("DLDocTagService - postAndUpdateDocumentTag method called...");

        DLDocumentTag dlDocumentTag;
        DLDocument dlDocument = dlDocumentRepository.findByIdAndArchivedFalseAndFolderFalse(tagDTODTO.getDocId());
        if (AppUtility.isEmpty(dlDocument)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        } else {
            dlDocumentTag = tagDTODTO.convertToEntity();
            dlDocumentTag.setUpdatedOn(ZonedDateTime.now());
            dlDocumentTagRepository.save(dlDocumentTag);

            DLDocumentActivity activity = new DLDocumentActivity(dlDocumentTag.getCreatedBy(), DLActivityTypeEnum.TAG_POSTED.getValue(),
                    dlDocumentTag.getId(), dlDocumentTag.getDlDocument().getId());
            dlDocumentActivityRepository.save(activity);
        }
        return dlDocumentTag;
    }
    public List<DLDocumentTagDTO> getAllTagsByDocumentId(Long documentId) {
        log.info("DLDocTagService - getAllTagsByDocumentId method called...");

        List<DLDocumentTagDTO> dlDocumentCommentDTOList = new ArrayList<>();
        List<DLDocumentTag> dlDocumentTagList = dlDocumentTagRepository.findAllByDlDocument_IdOrderByUpdatedOnDesc(documentId);
        for (DLDocumentTag documentTag : dlDocumentTagList) {
            DLDocumentTagDTO dto = new DLDocumentTagDTO();
            dto.convertToDTO(documentTag, false);

            Object response = restTemplate.getForObject("http://um-service/um/user/" + documentTag.getUpdatedBy(), Object.class);
            if (!AppUtility.isEmpty(response)) {
                HashMap<?, ?> map = (HashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("data");
                dto.setNameOfUser((String) map.get("name"));
            }
            dlDocumentCommentDTOList.add(dto);
        }
        return dlDocumentCommentDTOList;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void deleteDocumentTag(Long documentId) {
        log.info("DLDocCommentTag - deleteDocumentTag method called...");

        Optional<DLDocumentTag> dlDocumentTag = dlDocumentTagRepository.findById(documentId);
        if (dlDocumentTag.isPresent()) {
            dlDocumentTagRepository.delete(dlDocumentTag.get());

            DLDocumentActivity activity = new DLDocumentActivity(dlDocumentTag.get().getCreatedBy(), DLActivityTypeEnum.TAG_DELETED.getValue(),
                    dlDocumentTag.get().getId(), dlDocumentTag.get().getId());
            dlDocumentActivityRepository.save(activity);
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
    }

    public List<DLDocumentTag> searchTags(String message) {
        log.info("searchTags method called..");

        return advTagSearchRepository.searchTags(message);
    }

    public List<DLDocument> searchFavorite(String message) {
        log.info("searchFavorite method called..");

        return advTagSearchRepository.searchFavorite(message);
    }

    public List<DLDocument> searchShared(String message) {
        log.info("searchShared method called..");

        return advTagSearchRepository.searchShared(message);
    }
}
