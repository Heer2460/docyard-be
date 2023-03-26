package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentTag;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentTagDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import com.infotech.docyard.dochandling.service.DLDocTagService;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.CustomResponse;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dl-doc-tag")
@Log4j2
public class DLDocTagAPI {

    @Autowired
    private DLDocTagService tagService;

    @PostMapping(value = "/")
    public CustomResponse postAndUpdateDocumentTag(HttpServletRequest request,
                                              @RequestBody DLDocumentTagDTO tagDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("postAndUpdateDocumentTag API initiated...");

        if (AppUtility.isEmpty(tagDTO) || AppUtility.isEmpty(tagDTO.getDocId()) || !AppUtility.isEmpty(tagDTO.getId())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocumentTag dlDocumentTag = null;
        try {
            dlDocumentTag = tagService.postAndUpdateDocumentTag(tagDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocumentTag, new DLDocumentTagDTO(), false);
    }

    @PutMapping(value = "/")
    public CustomResponse updateDocumentTag(HttpServletRequest request,
                                                @RequestBody DLDocumentTagDTO tagDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("updateDocumentTag API initiated...");

        if (AppUtility.isEmpty(tagDTO) || AppUtility.isEmpty(tagDTO.getDocId()) || AppUtility.isEmpty(tagDTO.getId())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocumentTag dlDocumentTag = null;
        try {
            dlDocumentTag = tagService.postAndUpdateDocumentTag(tagDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocumentTag, new DLDocumentTagDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllTagsByDocumentId(HttpServletRequest request,
                                                     @RequestParam(value = "documentId", required = false) Long documentId) throws CustomException {
        log.info("getAllTagsByDocumentId API initiated...");

        List<DLDocumentTagDTO> dlDocumentTagDTOList = null;
        try {
            dlDocumentTagDTOList = tagService.getAllTagsByDocumentId(documentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(dlDocumentTagDTOList);
    }

    @RequestMapping(value = "/{documentId}", method = RequestMethod.DELETE)
    public CustomResponse deleteDocumentTag(HttpServletRequest request,
                                                @PathVariable(value = "documentId") Long documentId)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("deleteDocumentTag API initiated...");

        if (AppUtility.isEmpty(documentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        try {
            tagService.deleteDocumentTag(documentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("deleted.success"));
    }

    @GetMapping(value = "/tag/search/{userId}")
    public CustomResponse searchTag(HttpServletRequest request,
                                    @RequestParam(value = "searchKey") String searchKey,
                                    @PathVariable(value = "userId") Long userId) throws CustomException {
        log.info("searchTag API initiated...");

        List<DLDocumentTag> tagList = null;
        try {
            tagList = tagService.searchTags(searchKey);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(tagList, new DLDocumentTagDTO(), false);
    }

    @GetMapping(value = "/favorite/search/{userId}")
    public CustomResponse searchFavorite(HttpServletRequest request,
                                         @RequestParam(value = "searchKey") boolean searchKey,
                                         @PathVariable(value = "userId") Long userId) throws CustomException {
        log.info("searchFavorite API initiated...");

        List<DLDocument> tagList = null;
        try {
            tagList = tagService.searchFavorite(searchKey);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(tagList, new DLDocumentDTO(), false);
    }
    @GetMapping(value = "/shared/search/{userId}")
    public CustomResponse searchShared(HttpServletRequest request,
                                       @RequestParam(value = "searchKey") boolean searchKey,
                                       @PathVariable(value = "userId") Long userId) throws CustomException {
        log.info("searchShared API initiated...");

        List<DLDocument> tagList = null;
        try {
            tagList = tagService.searchShared(searchKey);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(tagList, new DLDocumentDTO(), false);
    }
}
