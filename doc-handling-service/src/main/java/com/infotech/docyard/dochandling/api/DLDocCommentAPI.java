package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.dto.DLDocumentCommentDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import com.infotech.docyard.dochandling.service.DLDocCommentService;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.CustomResponse;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dl-doc-comment")
@Log4j2
public class DLDocCommentAPI {

    @Autowired
    private DLDocCommentService dlDocCommentService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllCommentsByDocumentId(HttpServletRequest request,
                                                     @RequestParam(value = "documentId", required = false) Long documentId) throws CustomException {
        log.info("getAllCommentsByDocumentId API initiated...");

        List<DLDocumentCommentDTO> dlDocumentCommentDTOList = null;
        try {
            dlDocumentCommentDTOList = dlDocCommentService.getAllCommentsByDocumentId(documentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(dlDocumentCommentDTOList);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public CustomResponse postDocumentComment(HttpServletRequest request,
                                              @RequestBody DLDocumentCommentDTO commentDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("postDocumentComment API initiated...");

        if (AppUtility.isEmpty(commentDTO) || AppUtility.isEmpty(commentDTO.getDocId()) || !AppUtility.isEmpty(commentDTO.getId())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocumentComment dlDocumentComment = null;
        try {
            dlDocumentComment = dlDocCommentService.postAndUpdateDocumentComment(commentDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocumentComment, new DLDocumentCommentDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public CustomResponse updateDocumentComment(HttpServletRequest request,
                                                @RequestBody DLDocumentCommentDTO commentDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("updateDocumentComment API initiated...");

        if (AppUtility.isEmpty(commentDTO) || AppUtility.isEmpty(commentDTO.getDocId()) || AppUtility.isEmpty(commentDTO.getId())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocumentComment dlDocumentComment = null;
        try {
            dlDocumentComment = dlDocCommentService.postAndUpdateDocumentComment(commentDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocumentComment, new DLDocumentCommentDTO(), false);
    }

    @RequestMapping(value = "/{documentId}", method = RequestMethod.DELETE)
    public CustomResponse deleteDocumentComment(HttpServletRequest request,
                                                @PathVariable(value = "documentId") Long documentId)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("deleteDocumentComment API initiated...");

        if (AppUtility.isEmpty(documentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        try {
            dlDocCommentService.deleteDocumentComment(documentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("deleted.success"));
    }


}
